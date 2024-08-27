package org.uts.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 删除内存缓存中的商品售完标识
 * @Author codBoy
 * @Date 2024/8/25 15:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveSoldOutMessage {

    //秒杀商品ID
    private Long seckillId;

}
