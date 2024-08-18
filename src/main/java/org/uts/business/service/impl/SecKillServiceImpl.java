package org.uts.business.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.uts.business.service.product.ProductService;
import org.uts.global.constant.BusinessConstant;
import org.uts.global.constant.CacheConstant;
import org.uts.global.constant.OrderStatusEnum;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.SecKillService;
import org.uts.exception.BusinessException;
import org.uts.global.constant.PlatformTypeEnum;
import org.uts.global.errorCode.BusinessErrorCode;
import org.uts.service.order.OrderService;
import org.uts.utils.RedisLockUtils;
import org.uts.utils.RedisUtils;
import org.uts.utils.SnowflakeUtils;
import org.uts.vo.order.OrderVo;

import java.util.*;

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

    private static final Object SECKILL_LOCK = new Object();

    private SnowflakeUtils snowflakeUtils = new SnowflakeUtils(1, 1);

    /*
     * 秒杀接口
     */
    @Override
    public String secKill(ProductVo productVo) throws BusinessException {

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

        //更新商品库存: 该方法加了锁，为了缩小锁的粒度
        String orderId = this.updateStock(productVo, product.getTime());

        return orderId;
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
    public String updateStock(ProductVo productVo, Integer time) throws BusinessException {
        String lockKey = CacheConstant.SECKILL_PRODUCT_TIME_CACHE_KEY + BusinessConstant.COLON + time + BusinessConstant.COLON + productVo.getSeckillId();
        //存在问题：一直未获取到锁的线程，也执行释放锁的操作，会把真正加锁的线程的锁给释放掉，导致超卖问题,这里加入ThreadId，实现线程只释放自己的锁
        String threadId = String.valueOf(snowflakeUtils.nextId());

        try {
            int tryTime = 10;
            while(tryTime-- > 0) {
                //加锁
                log.info("Ready To Add Redis Lock, Thread ID: {}", threadId);
                boolean success = redisLockUtils.tryLockRedis(lockKey, threadId);
                //加锁失败
                if(!success) {
                    log.info("Add Redis Lock Failed, Thread ID: {}, Ready to Sleep and Retry ...", threadId);
                    Thread.sleep(30);
                }
                //加锁成功, 跳出循环
                else {
                    log.info("Add Redis Lock Success, Thread ID: {}", threadId);
                    break;
                }
            }
            if(tryTime <= 0) {
                throw new BusinessException(BusinessErrorCode.REDIS_TRY_LOCK_OVER_COUNT);
            }
            //业务操作
            return doSeckill(productVo);
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

        }
        return null;
    }

    /*
     判断库存操作、订单个数限制操作、扣库存操作、生成订单操作
     */
    public String doSeckill(ProductVo productVo) throws BusinessException{
        //TODO: 单个用户订单个数限制。可在缓存中添加用户下单标记，然后从缓存读取，判断是否重复下单，就不用加到锁里面了，减小锁粒度而提高并发性
        List<OrderVo> orderVoList = orderService.selectByUserId(productVo.getUserId());
        if(!CollectionUtils.isEmpty(orderVoList)) {
            throw new BusinessException(BusinessErrorCode.PRODUCT_COUNT_NOT_PERMITTED);
        }

        //需要再次查询库存信息，之前查询的库存信息不准确了
        ProductVo product = productService.selectById(productVo.getSeckillId());
        //如果库存不够，直接返回
        if(product.getStock() <= 0){
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_SALE_OVER);
        }
        //更新商品库存
        productService.updateStock(productVo.getSeckillId(), 1);

        //生成订单信息，发送到订单服务
        String orderId = String.valueOf(snowflakeUtils.nextId());
        OrderVo orderVo = new OrderVo(null, orderId, productVo.getUserId(), productVo.getSeckillId(), OrderStatusEnum.WAIT_TO_PAY_STATUS.getStatus(), null, PlatformTypeEnum.WEB_CLIENT_TYPE.getId(), null, null, null);
        int id = orderService.addOrder(orderVo);

        return orderId;
    }

}
