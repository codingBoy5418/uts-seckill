package org.uts.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.uts.global.constant.MqEnum;

/**
 * @Description 向MQ发送消息
 * @Author codBoy
 * @Date 2024/8/24 20:21
 */
@Configuration
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 向BUSINESS_QUEUE发送消息
     */
    public void sendMessage(@RequestBody String text){
        log.info("Send Message To MQ, Msg: {}", text);
    }
}
