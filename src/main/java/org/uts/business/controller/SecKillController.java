package org.uts.business.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.uts.business.domain.vo.SecKillProductPageVo;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.product.SecKillService;
import org.uts.business.service.product.ProductService;
import org.uts.exception.BusinessException;
import org.uts.global.constant.BusinessConstant;
import org.uts.result.RestResult;
import org.uts.service.order.OrderService;
import org.uts.valid.Add;
import org.uts.vo.order.OrderVo;
import org.uts.vo.order.RefundVo;

import java.util.List;

/**
 * @Description 秒杀商品 接口类
 * @Author codBoy
 * @Date 2024/7/14 18:58
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class SecKillController {

    @Autowired
    private SecKillService secKillProductService;

    @Autowired
    private ProductService productService;

    @Reference
    private OrderService orderService;

    /*
     * 查询秒杀商品列表信息 - 分页
     */
    @PostMapping("/page")
    public RestResult secKillProductPage(@RequestBody SecKillProductPageVo productPageVo){
        List<ProductVo> productVoList = productService.secKillProductPage(productPageVo);
        return RestResult.createSuccessfulRest(productVoList);
    }

    /*
     * 查询秒杀商品信息 - 详情
     */
    @GetMapping("/{id}")
    public RestResult detail(@PathVariable Long id){
        ProductVo productVo = productService.selectById(id);
        return RestResult.createSuccessfulRest(productVo);
    }

    /*
     * 秒杀接口
     */
    @PostMapping("/secKill")
    public RestResult secKill(@RequestBody @Validated(Add.class) ProductVo productVo) throws BusinessException {
        secKillProductService.secKill(productVo);
        return RestResult.createSuccessfulRest(BusinessConstant.SUCCESS);
    }

    /*
     * 付款接口
     */
    @PostMapping("/pay")
    public String pay(@RequestBody OrderVo orderVo) throws BusinessException {
        String res = secKillProductService.pay(orderVo);
        return res;
    }

    /*
     * 退款接口
     */
    @GetMapping("/refund")
    public RestResult refund(@RequestBody RefundVo refundVo) throws BusinessException {
        return RestResult.createSuccessfulRest(secKillProductService.refund(refundVo));
    }

}
