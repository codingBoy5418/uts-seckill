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

    //Redis锁
    public static final String REDIS_LOCK = "REDIS_LOCK";

    //缓存前缀
    public static final String CACHE_PREFIX = APP_NAME + COLON + MODULE_NAME;

    //分布式锁前缀
    public static final String REDIS_LOCK_PREFIX = APP_NAME + COLON + REDIS_LOCK;

    //秒杀商品 KEY
    public static final String SECKILL_PRODUCT_KEY = "SECKILL_PRODUCT";

    public static final String SECKILL_PRODUCT_CACHE_KEY = CACHE_PREFIX + COLON + SECKILL_PRODUCT_KEY;

    //秒杀商品id+场次Redis锁KEY
    public static final String SECKILL_PRODUCT_TIME_CACHE_KEY = REDIS_LOCK_PREFIX + COLON + "SECKILL_PRODUCT_TIME_LOCK";

    //秒杀商品库存缓存KEY
    public static final String SECKILL_PRODUCT_STOCK_CACHE_KEY = REDIS_LOCK_PREFIX + COLON + "PRODUCT_STOCK" + COLON;

    //用户是否重复下单缓存KEY
    public static final String SECKILL_ORDER_EXIST_KEY = REDIS_LOCK_PREFIX + COLON + "ORDER_SUCCESS" + COLON;

}
