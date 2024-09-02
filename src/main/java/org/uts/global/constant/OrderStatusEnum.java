package org.uts.global.constant;

/**
 * @Description 订单状态 枚举类
 * @Author codBoy
 * @Date 2024/8/15 22:37
 */
public enum OrderStatusEnum {
    WAIT_TO_PAY_STATUS(0, "待支付"),

    PAYED_STATUS(1, "已支付"),

    WAIT_TO_REFUND_STATUS(2, "待退款"),

    REFUNDED_STATUS(3, "已退款"),

    SYSTEM_CANCEL_STATUS(4, "自动取消"),

    HAND_CANCEL_STATUS(5, "手动取消"),
    ;


    //订单状态 - 数值
    private Integer status ;

    //订单状态 - 中文
    private String name;

    OrderStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
