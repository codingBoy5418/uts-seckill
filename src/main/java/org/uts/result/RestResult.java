package org.uts.result;

/**
 * @Author 86180
 * @Date 2022/12/7 23:51
 * @Version 1.0
 * @Description: 返回体
 **/
public class RestResult<T> extends BaseResult {

    public RestResult(){}

    public RestResult(T data, String code, String errorMsg, boolean success){
        super.data = data;
        super.code = code;
        super.errorMsg = errorMsg;
        super.success = success;
    }

    public RestResult(String code, String errorMsg){
        super.code = code;
        super.errorMsg = errorMsg;
    }

    public static <T> RestResult<T> createSuccessfulRest(T data){
        return createSuccessfulRest(data, "0", "", true);
    }

    public static RestResult createSuccessfulRest(){
        return createSuccessfulRest(null, "0", "", true);
    }

    public static RestResult createSuccessfulRest(String code, String errorMsg){
        return createSuccessfulRest(null, code, errorMsg, true);
    }

    public static <T> RestResult<T> createSuccessfulRest(T data, String code, String errorMsg, boolean success){
        RestResult restResult = new RestResult();
        restResult.data = data;
        restResult.code = code;
        restResult.errorMsg = errorMsg;
        restResult.success = success;
        return restResult;
    }

    public static RestResult createFailedResult(String code, String errorMsg){
        return createFailedResult(null, code, errorMsg, false);
    }

    public static <T> RestResult<T> createFailedResult(T data, String code, String errorMsg, boolean success){
        RestResult restResult = new RestResult();
        restResult.data = data;
        restResult.code = code;
        restResult.errorMsg = errorMsg;
        restResult.success = success;
        return restResult;
    }
}
