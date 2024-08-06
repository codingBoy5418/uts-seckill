package org.uts.business.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.uts.business.domain.vo.OrderVo;
import org.uts.business.service.OrderService;
import org.uts.result.RestResult;

/**
 * @Description 订单接口类
 * @Author codBoy
 * @Date 2024/8/1 21:00
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*
     * 查询订单信息接口
     */
    @GetMapping("/{id}")
    public RestResult order(@PathVariable String id){
        OrderVo orderVo = orderService.selectById(id);
        return RestResult.createSuccessfulRest(orderVo);
    }
}
