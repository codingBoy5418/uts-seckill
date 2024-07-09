package org.uts.exception;

/**
 * @Author 86180
 * @Date 2022/12/8 0:15
 * @Version 1.0
 * @Description: 系统异常类
 **/
public class SystemException extends BaseException{
    public SystemException(){
        super();
    }

    public SystemException(String errorCode, String errorMsg){
        super(errorCode, errorMsg);
    }
}
