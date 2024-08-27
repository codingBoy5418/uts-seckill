package org.uts.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 创建订单消息
 * @Author codBoy
 * @Date 2024/8/24 20:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderMessage {

    //用户ID
    private Long userId;

    //秒杀商品ID
    private Long seckillId;

    //下单平台
    private Integer platform;

}
