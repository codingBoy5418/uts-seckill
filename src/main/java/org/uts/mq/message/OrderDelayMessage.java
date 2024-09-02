package org.uts.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 下单延时消息
 * @Author codBoy
 * @Date 2024/8/24 22:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDelayMessage {
    //用户ID
    private Long userId;

    //商品ID
    private Long productId;

    //订单ID
    private String orderId;
}
