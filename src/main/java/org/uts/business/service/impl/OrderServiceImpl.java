package org.uts.business.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.uts.business.domain.vo.OrderVo;
import org.uts.business.service.OrderService;

/**
 * @Description 订单 服务实现类
 * @Author codBoy
 * @Date 2024/8/1 21:24
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    /*
     * 查询订单信息
     */
    public OrderVo selectById(String id) {
        return null;
    }
}
