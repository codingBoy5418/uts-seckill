package org.uts.exception;

import lombok.Data;
import org.uts.global.errorCode.ErrorCode;

/**
 * @Author 86180
 * @Date 2022/12/8 0:11
 * @Version 1.0
 * @Description:自定义异常基类
 **/
@Data
public abstract class BaseException extends Exception{

    //错误码
    private String errorCode;
    //错误描述
    private String errorMsg;

    protected BaseException(){}

    protected BaseException(String errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    protected BaseException(ErrorCode errorCode){
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getErrorMsg();
    }

}
