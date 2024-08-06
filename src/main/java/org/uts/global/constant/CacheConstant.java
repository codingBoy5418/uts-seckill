package org.uts.global.constant;

import static org.uts.global.constant.BusinessConstant.*;

/**
 * @Description 缓存常量类
 * @Author codBoy
 * @Date 2024/8/5 21:27
 */
public class CacheConstant {

    //项目名称
    public static final String APP_NAME = "UTS_SECKILL";

    //模块名称
    public static final String MODULE_NAME = "PRODUCT";

    //缓存前缀
    public static final String CACHE_PREFIX = APP_NAME + COLON + MODULE_NAME;

    //秒杀商品 KEY
    public static final String SECKILL_PRODUCT_KEY = "SECKILL_PRODUCT";

    public static final String SECKILL_PRODUCT_CACHE_KEY = CACHE_PREFIX + COLON + SECKILL_PRODUCT_KEY;
}