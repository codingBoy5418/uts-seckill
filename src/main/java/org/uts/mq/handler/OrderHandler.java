package org.uts.mq.handler;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uts.business.domain.vo.ProductVo;
import org.uts.business.service.product.SecKillService;
import org.uts.exception.BusinessException;
import org.uts.global.constant.*;
import org.uts.mq.message.AddOrderResultMessage;
import org.uts.mq.message.MQMessage;
import org.uts.mq.message.OrderDelayMessage;
import org.uts.mq.message.RemoveSoldOutMessage;
import org.uts.service.order.OrderService;
import org.uts.utils.SnowflakeUtils;
import org.uts.vo.order.OrderVo;

import java.util.Objects;
import java.util.UUID;

/**
 * @Description 订单MQ消息处理类
 * @Author codBoy
 * @Date 2024/8/24 21:09
 */
@Component
@Slf4j
public class OrderHandler {

    private final SnowflakeUtils snowflakeUtils = new SnowflakeUtils(1, 1);

    @Autowired
    private SecKillService secKillService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Reference
    private OrderService orderService;

    /*
     处理新增订单消息
     WS适用于服务端和客户端之间的场景。因此发送创建订单结果消息到MQ,由事件中心服务接收MQ消息，然后发送WS消息,由前端接收
     */
    public void dealWithAddOrderResult(AddOrderResultMessage addOrderResultMsg) {
        String success = addOrderResultMsg.getSuccess();
        //订单创建成功，发送订单超时延时消息
        if(BusinessConstant.SUCCESS.equals(success)) {
            OrderDelayMessage body = new OrderDelayMessage();
            BeanUtils.copyProperties(addOrderResultMsg, body);
            MQMessage mqMessage = new MQMessage(String.valueOf(UUID.randomUUID()), MessageCategory.BUSINESS_MESSAGE_TYPE, MessageType.ORDER_DELAY_MESSAGE_TYPE, JSON.toJSONString(body));
            rabbitTemplate.convertAndSend(
                    MqEnum.UTS_GENERAL_QUEUE.getExchange(),
                    MqEnum.UTS_GENERAL_QUEUE.getRoutingKey(),
                    JSON.toJSONString(mqMessage),
                    message -> {
                     message.getMessageProperties().setExpiration(String.valueOf(5 * 1000));
                     return message;
                    });
        }
        //订单创建失败，进行回滚操作
        else {
            ProductVo productVo = new ProductVo(addOrderResultMsg.getUserId(), addOrderResultMsg.getProductId());
            secKillService.failedRollback(productVo, true);
        }
    }

    /*
     处理删除订单售完标记消息
     */
    public void dealWithAddOrderResult(RemoveSoldOutMessage removeSoldOutMessage) {
        log.info("Delete Sold Out Flag From JVM Cache, SecKillId: {}", removeSoldOutMessage.getSeckillId());
        secKillService.removeSoldOver(removeSoldOutMessage.getSeckillId());
    }

    /*
     处理订单超时消息
     */
    public void dealWithOrderDelayResult(OrderDelayMessage orderTimeoutMessage) {
        try {
            OrderVo orderVo = new OrderVo();
            orderVo.setOrderId(orderTimeoutMessage.getOrderId());
            orderVo.setUserId(orderTimeoutMessage.getUserId());
            OrderVo orderVoFromDB = orderService.selectOrder(orderVo).get(0);
            //订单超时未支付
            if(Objects.equals(OrderStatusEnum.WAIT_TO_PAY_STATUS.getStatus(), orderVoFromDB.getStatus())
                    || Objects.equals(OrderStatusEnum.SYSTEM_CANCEL_STATUS.getStatus(), orderVoFromDB.getStatus())
                    || Objects.equals(OrderStatusEnum.HAND_CANCEL_STATUS.getStatus(), orderVoFromDB.getStatus())
            ){
                ProductVo productVo = new ProductVo();
                productVo.setSeckillId(orderTimeoutMessage.getProductId());
                productVo.setUserId(orderTimeoutMessage.getUserId());
                secKillService.failedRollback(productVo, true);
            }
        } catch (BusinessException businessException) {
            log.error("Order failedRollback Operation Failed, orderId: {}", orderTimeoutMessage.getOrderId());
            businessException.printStackTrace();
        }



    }

}
