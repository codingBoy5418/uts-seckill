package org.uts.filter;

/**
 * 请求上下文信息
 * **/
public class ContextHolder {
    public static ThreadLocal<Object> appContext = new ThreadLocal<>();

    /**
     * 设置参数
     * */
    public static void set(Object param){
        appContext.set(param);
    }

    /**
     * 获取参数
     * */
    public static Object get(){
        return appContext.get();
    }

    /**
     * 删除参数
     * */
    public static void remove(){
        appContext.remove();
    }

}
