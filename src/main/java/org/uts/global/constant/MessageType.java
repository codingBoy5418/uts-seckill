package org.uts.global.constant;

/**
 * @Description 消息类型枚举类
 * @Author codBoy
 * @Date 2024/8/24 20:57
 */
public class MessageType {

    //业务类型消息
    public static final String ADD_ORDER_MESSAGE_TYPE = "addOrder";

    //创建订单结果消息
    public static final String ADD_ORDER_RESULT_MESSAGE_TYPE = "addOrderResult";

    //删除内存缓存中的商品售完标记消息
    public static final String REMOVE_ORDER_SOLD_OUT_MESSAGE_TYPE = "removeOrderSoldOut";

    //订单延时消息
    public static final String ORDER_DELAY_MESSAGE_TYPE = "orderDelay";

}
