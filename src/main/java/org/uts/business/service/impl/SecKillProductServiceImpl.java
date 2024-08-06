package org.uts.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.uts.global.constant.CacheConstant.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.SecKillProductPageVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.mapper.SecKillProductMapper;
import org.uts.business.service.SecKillProductService;
import org.uts.utils.RedisUtils;

import java.util.ArrayList;
import java.util.Collections;
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
    public Long secKill(ProductVo productVo) {
        //秒杀商品场次判断，不在范围内则返回

        //用户订单个数限制

        //查询库存信息，如果库存不够，直接返回

        //下单

        return null;
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
