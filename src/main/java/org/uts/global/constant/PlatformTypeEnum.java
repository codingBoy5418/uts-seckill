package org.uts.global.constant;

import java.util.Objects;

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


    //平台 - 数值
    private Integer id ;

    //平台 - 中文
    private String name;

    PlatformTypeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
      根据平台数值，返回对应的平台名称
    */
    public static String getPlatformName(Integer id){
        String platformName = "未知";
        for(PlatformTypeEnum platformTypeEnum : PlatformTypeEnum.values()) {
            if(Objects.equals(platformTypeEnum.getId(), id)){
                platformName = platformTypeEnum.getName();
                break;
            }
        }

        return platformName;
    }
}
