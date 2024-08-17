package org.uts.business.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.uts.global.constant.CacheConstant.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.uts.global.constant.OrderStatusEnum;
import org.springframework.web.bind.annotation.PathVariable;
import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.SecKillProductPageVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.mapper.SecKillProductMapper;
import org.uts.business.service.SecKillProductService;
import org.uts.exception.BusinessException;
import org.uts.global.constant.PlatformTypeEnum;
import org.uts.global.errorCode.BusinessErrorCode;
import org.uts.service.order.OrderService;
import org.uts.utils.RedisUtils;
import org.uts.utils.SnowflakeUtils;
import org.uts.vo.order.OrderVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Description 秒杀商品 服务实现类
 * @Author codBoy
 * @Date 2024/7/14 20:03
 */
@Slf4j
@Service
public class SecKillProductServiceImpl implements SecKillProductService {

    @Autowired
    private SecKillProductMapper secKillProductMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Reference
    private OrderService orderService;

    /*
     * 查询秒杀商品列表信息 - 分页
     */
    @Override
    public List<ProductVo> secKillProductPage(SecKillProductPageVo productPageVo) {
        List<ProductVo> res = new ArrayList<>();

        //先从缓存中查找
        for(int time = 1; time <= 5; time++){
            Object object = redisUtils.hGet(SECKILL_PRODUCT_CACHE_KEY, String.valueOf(time));
            if(object == null){
                res.clear();
                break;
            }
            List<ProductVo> productVoList = JSON.parseArray((String) object, ProductVo.class);
            res.addAll(productVoList);
        }

        //缓存中没有数据，则从数据库中查找数据
        if(CollectionUtils.isEmpty(res)){
            PageHelper.startPage(productPageVo.getPageNum(),productPageVo.getPageSize());
            List<ProductDto> productDtoList = secKillProductMapper.selectProduct(productPageVo);
            res.addAll(ProductDto.convertToVo(productDtoList));
        }

        return res;
    }

    /*
     * 查询秒杀商品信息 - 详情
     */
    @Override
    @Cacheable(key = "'selectById_' + #id", value = "SecKillProductServiceImpl")
    public ProductVo selectById(@PathVariable Long id){
        ProductDto productDto = secKillProductMapper.selectById(id);
        if(productDto == null){
            return null;
        }
        return ProductDto.convertToVo(Collections.singletonList(productDto)).get(0);
    }

    /*
     * 秒杀接口
     */
    @Override
    public String secKill(ProductVo productVo) throws BusinessException {

        //商品不存在，直接返回
        ProductDto productDto = secKillProductMapper.selectById(productVo.getSeckillId());
        if(productDto == null){
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_NOT_EXIST);
        }

        //秒杀商品场次判断，不在范围内则返回
        Date now = new Date();
        if(now.before(productDto.getStartTime()) || now.after(productDto.getEndTime())) {
            throw new BusinessException(BusinessErrorCode.TIME_IS_NOT_IN_RANGE);
        }

        //查询库存信息，如果库存不够，直接返回
        if(productDto.getStock() <= 0){
            throw new BusinessException(BusinessErrorCode.PRODUCT_IS_SALE_OVER);
        }

        //用户订单个数限制
        List<OrderVo> orderVoList = orderService.selectByUserId(productVo.getUserId());
        if(!CollectionUtils.isEmpty(orderVoList)) {
            throw new BusinessException(BusinessErrorCode.PRODUCT_COUNT_NOT_PERMITTED);
        }

        //更新商品库存
        int n = secKillProductMapper.updateStock(productVo.getSeckillId(), 1);

        //生成订单信息，发送到订单服务
        String orderId = String.valueOf(new SnowflakeUtils(1, 1).nextId());
        OrderVo orderVo = new OrderVo(null, orderId, productVo.getUserId(), productVo.getSeckillId(), OrderStatusEnum.WAIT_TO_PAY_STATUS.getStatus(), null, PlatformTypeEnum.WEB_CLIENT_TYPE.getId(), null, null, null);
        int id = orderService.addOrder(orderVo);

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
    public String refund(String id) {
      return null;
    }


    /*
      查询当天的秒杀商品信息
     */
    public List<ProductDto> selectCurDayProduct(){
        return secKillProductMapper.selectCurDayProduct();
    }
}
