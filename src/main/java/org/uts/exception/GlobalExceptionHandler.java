package org.uts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.uts.global.errorCode.ErrorCode;
import org.uts.result.RestResult;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理
 * **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理 form data方式调用接口校验失败抛出的异常 (对象参数)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResult error(BindException e) {

        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder(ErrorCode.COMMON_ILLEGAL_ARGUMENT.getErrorMsg());
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(",参数:").append(fieldError.getField());
        }
        return RestResult.createFailedResult(ErrorCode.COMMON_ILLEGAL_ARGUMENT.getCode(), sb.toString());
    }

    /**
     * 处理 json 请求体调用接口校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResult error(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder(ErrorCode.COMMON_ILLEGAL_ARGUMENT.getErrorMsg());
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(",参数:").append(fieldError.getField());
        }
        return RestResult.createFailedResult(ErrorCode.COMMON_ILLEGAL_ARGUMENT.getCode(), sb.toString());
    }

    /**
     * 单个参数校验失败抛出的异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResult error(ConstraintViolationException e) {
        return RestResult.createFailedResult("500", e.toString());
    }

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResult error(BusinessException e) {
        return RestResult.createFailedResult(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 系统异常处理
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RestResult error(SystemException e) {
        return RestResult.createFailedResult(e.getErrorCode(), e.getErrorMsg());
    }
}
