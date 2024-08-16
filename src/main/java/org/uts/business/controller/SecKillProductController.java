package org.uts.business.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.uts.business.domain.vo.BatchAddProductVo;
import org.uts.business.domain.vo.SecKillProductPageVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.SecKillProductService;
import org.uts.exception.BusinessException;
import org.uts.result.RestResult;
import org.uts.service.order.OrderService;
import org.uts.valid.Add;

import java.util.List;

/**
 * @Description 秒杀商品 接口类
 * @Author codBoy
 * @Date 2024/7/14 18:58
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class SecKillProductController {

    @Autowired
    private SecKillProductService secKillProductService;

    @Reference
    private OrderService orderService;

    /*
     * 查询秒杀商品列表信息 - 分页
     */
    @PostMapping("/page")
    public RestResult secKillProductPage(@RequestBody SecKillProductPageVo productPageVo){
        List<ProductVo> productVoList = secKillProductService.secKillProductPage(productPageVo);
        return RestResult.createSuccessfulRest(productVoList);
    }

    /*
     * 查询秒杀商品信息 - 详情
     */
    @GetMapping("/{id}")
    public RestResult detail(@PathVariable Long id){
        ProductVo productVo = secKillProductService.selectById(id);
        return RestResult.createSuccessfulRest(productVo);
    }

    /*
     * 秒杀接口
     */
    @PostMapping("/secKill")
    public RestResult secKill(@RequestBody @Validated(Add.class) ProductVo productVo) throws BusinessException {
        String id = secKillProductService.secKill(productVo);
        return RestResult.createSuccessfulRest(id);
    }

    /*
     * 付款接口
     */
    @PostMapping("/pay")
    public RestResult pay(@RequestBody BatchAddProductVo batchAddProductVo){
        String id = secKillProductService.pay(batchAddProductVo);
        return RestResult.createSuccessfulRest(id);
    }

    /*
     * 退款接口
     */
    @GetMapping("/refund/{id}")
    public RestResult refund(@PathVariable String id){
        String res = secKillProductService.refund(id);
        return RestResult.createSuccessfulRest(res);
    }

}
