package org.uts.exception;


import org.uts.global.errorCode.ErrorCode;

/**
 * @Author 86180
 * @Date 2022/12/8 0:15
 * @Version 1.0
 * @Description: 业务异常类
 **/
public class BusinessException extends BaseException {

    public BusinessException(){
        super();
    }

    public BusinessException(String errorCode, String errorMsg){
        super(errorCode, errorMsg);
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode);
    }

}
