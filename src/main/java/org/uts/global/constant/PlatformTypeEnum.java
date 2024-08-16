package org.uts.global.constant;

/**
 * @Description 平台类型 枚举类
 * @Author codBoy
 * @Date 2024/8/15 22:37
 */
public enum PlatformTypeEnum {
    WEB_CLIENT_TYPE(0, "WEB端"),

    PC_CLIENT_TYPE(1, "PC端"),

    APP_CLIENT_TYPE(2, "APP端"),

    CODE_CLIENT_TYPE(3, "微信小程序"),

    ;


    //订单状态 - 数值
    private Integer status ;

    //订单状态 - 中文
    private String name;

    PlatformTypeEnum(Integer status, String name) {
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
