package org.uts.business.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.uts.business.service.product.ProductService;
import org.uts.global.constant.OrderStatusEnum;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.SecKillService;
import org.uts.exception.BusinessException;
import org.uts.global.constant.PlatformTypeEnum;
import org.uts.global.errorCode.BusinessErrorCode;
import org.uts.service.order.OrderService;
import org.uts.utils.RedisUtils;
import org.uts.utils.SnowflakeUtils;
import org.uts.vo.order.OrderVo;

import java.util.Date;
import java.util.List;

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

    private static final Object SECKILL_LOCK = new Object();

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
        String orderId = this.updateStock(productVo);

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
     */
    public String updateStock(ProductVo productVo) throws BusinessException {
        synchronized (SECKILL_LOCK) {
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
            String orderId = String.valueOf(new SnowflakeUtils(1, 1).nextId());
            OrderVo orderVo = new OrderVo(null, orderId, productVo.getUserId(), productVo.getSeckillId(), OrderStatusEnum.WAIT_TO_PAY_STATUS.getStatus(), null, PlatformTypeEnum.WEB_CLIENT_TYPE.getId(), null, null, null);
            int id = orderService.addOrder(orderVo);

            return orderId;
        }
    }
}
