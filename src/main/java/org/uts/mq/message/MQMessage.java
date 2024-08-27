package org.uts.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description MQ消息类
 * @Author codBoy
 * @Date 2024/8/24 20:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MQMessage {

    //消息id
    private String id;

    //消息大类：business,status,alarm等
    private String category;

    //消息小类：具体的消息类型
    private String type;

    //消息体
    private String body;




}
