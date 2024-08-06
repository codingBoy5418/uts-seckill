package org.uts.business.service;

import org.uts.business.domain.vo.OrderVo;

/**
 * @Description 订单接口
 * @Author codBoy
 * @Date 2024/8/1 21:23
 */
public interface OrderService {

    /*
     * 查询订单信息
     */
    OrderVo selectById(String id);
}
