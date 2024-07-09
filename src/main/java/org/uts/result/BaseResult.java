package org.uts.result;

import lombok.Data;

/**
 * @Author 86180
 * @Date 2022/12/7 23:48
 * @Version 1.0
 * @Description: 返回体
 **/
@Data
public abstract class BaseResult {
    //错误码
    public String code;
    //错误信息
    public String errorMsg;
    //数据
    public Object data;
    //请求结果
    public boolean success;
}
