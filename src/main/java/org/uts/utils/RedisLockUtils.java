package org.uts.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 基于Redis的分布式锁 工具类
 * @Author codBoy
 * @Date 2024/8/18 16:38
 */
@Component
public class RedisLockUtils {

    @Autowired
    private RedisUtils redisUtils;

    /*
     获取 分布式锁
     */
    public boolean tryLockRedis(String key, Object value) {
        try {
            return redisUtils.setIfAbsent(key, value);
        }
        catch (Exception e){
            return false;
        }
    }

    /*
     释放 分布式锁
     */
    public boolean unLockRedis(String key) {
        try {
            return redisUtils.del(key);
        }
        catch (Exception e){
            return false;
        }
    }
}
