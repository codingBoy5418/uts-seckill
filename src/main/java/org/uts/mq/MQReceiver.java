package org.uts.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import org.uts.global.constant.MessageType;
import org.uts.mq.handler.OrderHandler;
import org.uts.mq.message.AddOrderResultMessage;
import org.uts.mq.message.MQMessage;
import org.uts.mq.message.RemoveSoldOutMessage;

import java.util.Date;

/**
 * @Description 向MQ接收消息
 * @Author codBoy
 * @Date 2024/8/24 20:11
 */
@Component
@Slf4j
public class MQReceiver {

    //秒杀队列
    private static final String UTS_SECKILL_QUEUE = "UTS_SECKILL_QUEUE";

    //删除内存缓存中的商品售完队列
    private static final String REMOVE_SOLD_OUT_QUEUE = "REMOVE_SOLD_OUT_QUEUE";

    @Autowired
    private OrderHandler orderHandler;

    /**
     * 接收消息
     */
    @RabbitListener(queues = UTS_SECKILL_QUEUE)
    public void receiveSecKillMessage(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("{}，接收到MQ消息，消息内容：{}", new Date().toString(), msg);
        MQMessage mqMessage = JSON.parseObject(msg, MQMessage.class);
        //处理订单结果消息
        if(MessageType.ADD_ORDER_RESULT_MESSAGE_TYPE.equals(mqMessage.getType())) {
            orderHandler.dealWithAddOrderResult(JSON.parseObject(mqMessage.getBody(), AddOrderResultMessage.class));
        }
        //处理删除订单售完标记消息
        if(MessageType.REMOVE_ORDER_SOLD_OUT_MESSAGE_TYPE.equals(mqMessage.getType())) {
            orderHandler.dealWithAddOrderResult(JSON.parseObject(mqMessage.getBody(), RemoveSoldOutMessage.class));
        }
    }
}
