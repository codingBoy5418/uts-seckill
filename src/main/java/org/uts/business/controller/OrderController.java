package org.uts.business.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.uts.exception.BusinessException;
import org.uts.result.RestResult;
import org.uts.service.order.OrderService;
import org.uts.vo.order.OrderVo;

/**
 * @Description 订单接口类
 * @Author codBoy
 * @Date 2024/8/1 21:00
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /*
     * 查询订单详情信息接口
     */
    @PostMapping("/detail")
    public RestResult order(@RequestBody OrderVo orderVo) throws BusinessException {
        return RestResult.createSuccessfulRest(orderService.selectOrder(orderVo));
    }
}
