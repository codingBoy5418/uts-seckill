package org.uts.global.errorCode;

/**
 * @Description 业务错误码
 * @Author codBoy
 * @Date 2024/8/6 21:35
 */
public enum BusinessErrorCode {

    //******************************* Redis操作 ******************************
    REDIS_TRY_LOCK_OVER_COUNT("010001", "Redis加锁操作过于频繁"),

    //******************************* 商品业务 ******************************
    PRODUCT_IS_NOT_EXIST("020001", "商品不存在"),

    TIME_IS_NOT_IN_RANGE("020002", "不在秒杀时间范围内"),

    PRODUCT_IS_SALE_OVER("020003", "商品已卖完"),

    PRODUCT_COUNT_NOT_PERMITTED("020004", "商品购买数量超出限制"),

    ;


    //错误码
    private String code;

    //错误消息
    private String errorMsg;

    BusinessErrorCode(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
