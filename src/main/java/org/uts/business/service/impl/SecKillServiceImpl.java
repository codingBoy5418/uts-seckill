package org.uts.business.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.uts.business.service.product.ProductService;
import org.uts.global.constant.*;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.SecKillService;
import org.uts.exception.BusinessException;
import org.uts.global.errorCode.BusinessErrorCode;
import org.uts.mq.message.AddOrderMessage;
import org.uts.mq.message.MQMessage;
import org.uts.mq.message.RemoveSoldOutMessage;
import org.uts.service.order.OrderService;
import org.uts.utils.RedisLockUtils;
import org.uts.utils.RedisUtils;
import org.uts.utils.SnowflakeUtils;
import org.uts.vo.order.OrderVo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static org.uts.global.constant.CacheConstant.*;

/**
 * @Description 秒杀商品 服务实现类
 * @Author codBoy
 * @Date 2024/7/14 20:03
 */
@Slf4j
@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisUtils redisUtils;

    @Reference
    private OrderService orderService;

    @Autowired
    private RedisLockUtils redisLockUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Object SECKILL_LOCK = new Object();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //商品库存缓存: KEY -> 秒杀商品id; VALUE -> 商品是否卖完
    public static final Map<Long, Boolean> SECKILL_PRODUCT_STOCK_CACHE = new ConcurrentHashMap<>();

    private SnowflakeUtils snowflakeUtils = new SnowflakeUtils(1, 1);

    @Autowired
    private RedisScript<Boolean> redisScript;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    /*
     * 秒杀接口
     */
    @Override
    public void secKill(ProductVo productVo) throws BusinessException {
        //从秒杀商品库存缓存中，判断商品是否卖完
        Boolean isSellOver = SECKILL_PRODUCT_STOCK_CACHE.get(productVo.getSeckillId());
        if(isSellOver != null && isSellOver) {
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_SALE_OVER);
        }

        //商品不存在，直接返回
        ProductVo product = productService.selectById(productVo.getSeckillId());
        if(product == null){
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_NOT_EXIST);
        }

        //秒杀商品场次判断，不在范围内则返回
        Date now = new Date();
        if(now.before(product.getStartTime()) || now.after(product.getEndTime())) {
            throw new BusinessException(BusinessErrorCode.TIME_IS_NOT_IN_RANGE);
        }

        //判断用户是否重复下单：Redis的PUTNX命令,后改成increment命令，这样可以控制数量
        long orderCount = redisTemplate.opsForHash().increment(CacheConstant.SECKILL_ORDER_EXIST_KEY + product.getTime(), String.valueOf(productVo.getUserId()), 1);
        if(orderCount > 1){
            throw new BusinessException(BusinessErrorCode.PRODUCT_COUNT_NOT_PERMITTED);
        }

        //判断库存是否充足（库存预减实现）
        Long remain = redisTemplate.opsForHash().increment(SECKILL_PRODUCT_STOCK_CACHE_KEY + product.getTime(), String.valueOf(productVo.getSeckillId()), -1);
        if(remain < 0){
            //商品库存不够，将商品信息缓存到内存缓存中
            SECKILL_PRODUCT_STOCK_CACHE.put(productVo.getSeckillId(), true);
            //商品库存不够，删除用户重复下单标志
            redisTemplate.opsForHash().delete(CacheConstant.SECKILL_ORDER_EXIST_KEY + product.getTime(), String.valueOf(productVo.getUserId()));
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_SALE_OVER);
        }

        //更新商品库存[悲观锁]: 该方法加了锁，为了缩小锁的粒度
        this.updateStock(productVo, product.getTime());
        //更新商品库存[乐观锁]: 该方法加了锁，为了缩小锁的粒度
        //String orderId = this.updateStock(productVo);

    }

    /*
     * 支付接口
     */
    @Override
    public String pay(BatchAddProductVo batchAddProductVo) {
        return null;
    }

    /*
     * 退款接口
     */
    @Override
    public String refund(String id) {
      return null;
    }


    /*
     * 更新商品库存
     * TODO: 存在问题:一直未获取到锁的线程,也执行释放锁的操作,会把真正加锁的线程的锁给释放掉,导致超卖问题
     *  解决：Redis 的 Set 命令,所有线程 key 保持一致,value存线程ID,删除时删除自己线程加的锁
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(ProductVo productVo, Integer time) throws BusinessException {
        log.info("Into updateStock Method,Ready To Update Stock Operation...");
        productVo.setTime(time);
        String lockKey = CacheConstant.SECKILL_PRODUCT_TIME_CACHE_KEY + BusinessConstant.COLON + time + BusinessConstant.COLON + productVo.getSeckillId();
        //存在问题：一直未获取到锁的线程，也执行释放锁的操作，会把真正加锁的线程的锁给释放掉，导致超卖问题,这里加入ThreadId，实现线程只释放自己的锁
        String threadId = String.valueOf(snowflakeUtils.nextId());

        //过期时间
        int timeout = 2;
        //任务线程池
        ScheduledFuture<?> future = null;

        try {
            //重试次数需要设置大点，让线程有机会获取到锁
            int tryTime = 50;
            while(tryTime-- > 0) {
                //加锁
                log.info("Ready To Add Redis Lock, Thread ID: {}", threadId);
                //boolean success = redisLockUtils.tryLockRedis(lockKey, threadId);
                Boolean success = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), threadId, timeout);
                //加锁失败
                if(!success) {
                    //睡眠时间值可以设置小点。高并发场景下，无论取何值，大家基本都是同时睡眠，然后同时争抢锁，因此与大小无关
                    log.info("Add Redis Lock Failed, Thread ID: {}, Ready to Sleep and Retry ...", threadId);
                    Thread.sleep(10);
                }
                //加锁成功, 跳出循环
                else {
                    log.info("Add Redis Lock Success, Thread ID: {}", threadId);
                    break;
                }
            }
            if(tryTime <= 0) {
                //如果超过次数，还没获取到锁，需要删除用户下单标识、更新库存、删除商品售完标识，让其他线程进来
                log.info("The Number Of Locking Exceeds 10 Times, Throw an Exception...");
                this.failedRollback(productVo, false);
                throw new BusinessException(BusinessErrorCode.REDIS_TRY_LOCK_OVER_COUNT);
            }

            //执行业务前开启看门狗，实现自动续期
//            future = scheduledExecutorService.scheduleAtFixedRate(
//                    () -> {
//                        //查询分布式缓存的key是否存在，若存在，在续期；否则，不续期
//                        String cacheThreadId = (String) redisUtils.get(lockKey);
//                        if(!StringUtils.isEmpty(cacheThreadId) && cacheThreadId.equals(threadId)){
//                            log.info("Watch Dog Execute Add Time Operation,Thread ID: {}", threadId);
//                            redisUtils.expire(lockKey, timeout + 2, TimeUnit.SECONDS);
//                            log.info("Watch Dog Add Time Operation Over,Thread ID: {}", threadId);
//                        }
//                    },
//                    timeout - 2,
//                    timeout,
//                    TimeUnit.SECONDS
//            );

            //业务操作
            doSeckill(productVo);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Redis TryLock Operation has a Exception, Thread ID: {}", threadId);
        } finally {
            String cacheThreadId = (String)redisUtils.get(lockKey);
            if(threadId.equals(cacheThreadId)){
                //释放锁
                log.info("Ready to Release Redis Lock, Thread ID: {}", threadId);
                boolean success = redisLockUtils.unLockRedis(lockKey);
                //处理释放失败的情况
                if(!success) {
                    log.error("Release Redis Lock Field, Thread ID: {}", threadId);
                }
                else {
                    log.info("Release Redis Lock Success, Thread ID: {}", threadId);
                }
            }

            //销毁任务,注意这里销毁的是任务不是线程。销毁线程后,任务还在,因此还会产生任务对应的线程
            log.info("Cancel Add Time Task");
            if(future != null){
                future.cancel(true);
            }
        }
    }

    /*
      TODO: 乐观锁实现: sql语句扣库存时添加条件：WHERE STOCK > 0, 当扣除库存失败时，抛异常;
            利用的是乐观锁的思想;
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateStock(ProductVo productVo) throws BusinessException {
        //TODO: 单个用户订单个数限制。可在缓存中添加用户下单标记，然后从缓存读取，判断是否重复下单，就不用加到锁里面了，减小锁粒度而提高并发性
        List<OrderVo> orderVoList = orderService.selectByUserId(productVo.getUserId());
        if(!CollectionUtils.isEmpty(orderVoList)) {
            throw new BusinessException(BusinessErrorCode.PRODUCT_COUNT_NOT_PERMITTED);
        }

        //更新商品库存
        int n = productService.updateStock(productVo.getSeckillId(), 1);
        if(n <= 0) {
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_SALE_OVER);
        }

        //生成订单信息，发送到订单服务
        String orderId = String.valueOf(snowflakeUtils.nextId());
        OrderVo orderVo = new OrderVo(null, orderId, productVo.getUserId(), productVo.getSeckillId(), OrderStatusEnum.WAIT_TO_PAY_STATUS.getStatus(), null, PlatformTypeEnum.WEB_CLIENT_TYPE.getId(), null, null, null);
        int id = orderService.addOrder(orderVo);

        return orderId;
    }

    /*
     判断库存操作、订单个数限制操作、扣库存操作、生成订单操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void doSeckill(ProductVo productVo) throws BusinessException{
        //TODO: 单个用户订单个数限制。可在缓存中添加用户下单标记，然后从缓存读取，判断是否重复下单，就不用加到锁里面了，减小锁粒度而提高并发性
//        List<OrderVo> orderVoList = orderService.selectByUserId(productVo.getUserId());
//        if(!CollectionUtils.isEmpty(orderVoList)) {
//            throw new BusinessException(BusinessErrorCode.PRODUCT_COUNT_NOT_PERMITTED);
//        }

        //需要再次查询库存信息，之前查询的库存信息不准确了
//        ProductVo product = productService.selectById(productVo.getSeckillId());
//        //如果库存不够，直接返回
//        if(product.getStock() <= 0){
//            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_SALE_OVER);
//        }


        //更新商品库存
        try {
            productService.updateStock(productVo.getSeckillId(), 1);

            //生成订单信息，发送到订单服务
//        String orderId = String.valueOf(snowflakeUtils.nextId());
//        OrderVo orderVo = new OrderVo(null, orderId, productVo.getUserId(), productVo.getSeckillId(), OrderStatusEnum.WAIT_TO_PAY_STATUS.getStatus(), null, PlatformTypeEnum.WEB_CLIENT_TYPE.getId(), null, null, null);
//        int id = orderService.addOrder(orderVo);

            //优化：为提高吞吐量，采用异步方式代替RPC远程调用新增订单：向MQ发送订单消息
            AddOrderMessage addOrderMessage = new AddOrderMessage(productVo.getUserId(), productVo.getSeckillId(), PlatformTypeEnum.WEB_CLIENT_TYPE.getId());
            MQMessage mqMessage = new MQMessage(UUID.randomUUID().toString(), MessageCategory.BUSINESS_MESSAGE_TYPE, MessageType.ADD_ORDER_MESSAGE_TYPE, JSON.toJSONString(addOrderMessage));
            log.info("Send CREATE_ORDER_QUEUE Message To uts-order Service...");
            rabbitTemplate.convertAndSend(MqEnum.UTS_ORDER_QUEUE.getExchange(), MqEnum.UTS_ORDER_QUEUE.getRoutingKey(), JSON.toJSONString(mqMessage));

        } catch (Exception e) {
            e.printStackTrace();
            failedRollback(productVo, false);
        }
    }

    /*
     异常回滚: (1)删除Redis中用户下单标识;
              (2)更新Redis中秒杀商品库存;
              (3)删除内存缓存中的商品售完标识(可以不删除，为安全起见删除);
     */
    public void failedRollback(ProductVo productVo, boolean isUpdateStock) {

        //更新Redis中秒杀商品库存;
        ProductVo product = productService.selectById(productVo.getSeckillId());
        redisTemplate.opsForHash().put(SECKILL_PRODUCT_STOCK_CACHE_KEY + product.getTime(), String.valueOf(product.getSeckillId()), product.getStock());

        //删除Redis中用户下单标识
        redisTemplate.opsForHash().delete(CacheConstant.SECKILL_ORDER_EXIST_KEY + product.getTime(), String.valueOf(productVo.getUserId()));

        //删除内存缓存中的商品售完标识(可以不删除，为安全起见删除),考虑到集群部署的情况，需要发送到MQ中、
        RemoveSoldOutMessage body = new RemoveSoldOutMessage(product.getSeckillId());
        MQMessage mqMessage = new MQMessage(UUID.randomUUID().toString(), MessageCategory.BUSINESS_MESSAGE_TYPE, MessageType.REMOVE_ORDER_SOLD_OUT_MESSAGE_TYPE, JSON.toJSONString(body));
        log.info("Send REMOVE_ORDER_SOLD_OUT_MESSAGE_TYPE MQ Message, Msg: {}", mqMessage);
        rabbitTemplate.convertAndSend(MqEnum.UTS_SECKILL_QUEUE.getExchange(), MqEnum.UTS_SECKILL_QUEUE.getRoutingKey(), JSON.toJSONString(mqMessage));

    }

    /*
     * 订单创建异常，回滚操作
     */
    public void removeSoldOver(Long secKillId) {
        Boolean soldFlag = SECKILL_PRODUCT_STOCK_CACHE.get(secKillId);
        if(soldFlag != null){
            SECKILL_PRODUCT_STOCK_CACHE.remove(secKillId);
        }
    }
}
