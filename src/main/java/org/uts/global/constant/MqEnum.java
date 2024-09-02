package org.uts.global.constant;

/**
 * @Author 86180
 * @Date 2022/12/22 15:15
 * @Version 1.0
 * @Description: mq常量类
 **/
public enum MqEnum {

    //事件中心业务队列
    UTS_EVENT_BUSINESS_QUEUE("BUSINESS_EXCHANGE", "UTS_EVENT_BUSINESS_QUEUE", "uts_event_business"),

    //订单队列
    UTS_ORDER_QUEUE("BUSINESS_EXCHANGE", "UTS_ORDER_QUEUE", "uts_order"),

    //秒杀队列
    UTS_SECKILL_QUEUE("BUSINESS_EXCHANGE", "UTS_SECKILL_QUEUE", "uts_seckill"),

    //一般队列，用于接收延时消息
    UTS_GENERAL_QUEUE("BUSINESS_EXCHANGE", "UTS_GENERAL_QUEUE", "uts_general"),

    ;


    //业务交换机
    public String exchange;
    //队列
    public String queue;
    //路邮键
    public String routingKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    MqEnum(String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
    }
}
