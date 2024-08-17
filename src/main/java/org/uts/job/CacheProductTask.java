package org.uts.job;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uts.business.domain.dto.ProductDto;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.product.ProductService;
import org.uts.utils.RedisUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.uts.global.constant.CacheConstant.*;

/**
 * @Description 在每天的 00:00:00定时删除前一天的秒杀商品缓存信息，然后缓存当前的秒杀商品信息
 * @Author codBoy
 * @Date 2024/8/5 21:19
 */
@Slf4j
@Component
public class CacheProductTask {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProductService productService;

    /**
    * 删除前一天的秒杀商品缓存信息，新增当天的秒杀商品信息
    */
    @XxlJob("cacheProductJob")
    public void addOrRemoveProductCache() {
        //删除前一天的秒杀商品缓存信息
        log.info("Start to remove expire time seckill product cache info, task name: cacheProductJob ...");
        redisUtils.del(SECKILL_PRODUCT_CACHE_KEY);

        //新增当天的秒杀商品缓存信息
        log.info("Start to add today seckill product cache info, task name: cacheProductJob ...");
        List<ProductDto> todayProductList = productService.selectCurDayProduct();
        Map<Integer, List<ProductDto>> productIdMap = todayProductList.stream().collect(Collectors.groupingBy(ProductDto :: getTime));
        for(Map.Entry<Integer, List<ProductDto>> entry : productIdMap.entrySet()){
            Integer time = entry.getKey();
            List<ProductDto> productDtoList = entry.getValue();
            List<ProductVo> productVoList = ProductDto.convertToVo(productDtoList);
            redisUtils.hSet(SECKILL_PRODUCT_CACHE_KEY,  String.valueOf(time), JSON.toJSONString(productVoList));
        }
        log.info("add today seckill product cache info over, task name: cacheProductJob ...");
    }
}
