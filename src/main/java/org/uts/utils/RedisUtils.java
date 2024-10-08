package org.uts.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.time.Duration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 参考自：<a href="https://blog.csdn.net/mengxiangxingdong/article/details/88419976">...</a>
 * **/
@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ============================= common ============================
    /**
     * 指定缓存失效时间
     *
     * @param key 键
     * @param time 时间
     * @param unit 时间单位
     */
    public boolean expire(String key, long time, TimeUnit unit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, unit);
            }
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key    键
     * @return       true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 删除缓存 - 单个
     *
     * @param key 只可以传一个值
     */
    public boolean del(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    // ============================ String =============================
    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return    值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key    键
     * @param value  值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key    键
     * @param value  值
     * @param time   时间(秒) time要大于0, 如果time小于等于0 将设置无限期
     * @return       true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 若不存在,设置值; 可用于Redis分布式锁
     * 底层用的是:     SETNX命令,无对应的过期时间命令
     * @param key    键
     * @param value  值
     * @return       是否成功
     */
    public boolean setIfAbsent(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 若不存在,设置值; 可用于Redis分布式锁
     * 底层用的是:     SET命令
     * @param key    键
     * @param value  值
     * @param time   时间,单位为秒
     * @return 是否成功
     */
    public boolean setIfAbsent(String key, Object value, long time) {
        try {
            redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(time));
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 递增 适用场景： https://blog.csdn.net/y_y_y_k_k_k_k/article/details/79218254 高并发生成订单号，秒杀类的业务逻辑等。。
     *
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return 当前数
     */
    public long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key    键
     * @param delta 要减少几(小于0)
     * @return 当前数
     */
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    // ================================ Map =================================
    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return     值
     */
    public Object hGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key    键
     * @param item   项
     * @param value  值
     * @return true 成功 false失败
     */
    public boolean hSet(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key    键
     * @param item   项
     * @param value  值
     * @param time   时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true  成功 false失败
     */
    public boolean hSet(String key, String item, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key   键 不能为null
     * @param item  项 可以使多个,不能为null
     */
    public void hDelete(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key   键 不能为null
     * @param item  项 不能为null
     * @return      true-存在; false-不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return    对应的多个键值
     */
    public Map<Object, Object> hmGet(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet设置值
     *
     * @param key    键
     * @param map    对应多个键值
     * @return true  成功 false 失败
     */
    public boolean hmSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key   键
     * @param map   对应多个键值
     * @param time  时间(秒)
     * @return true 成功 false失败
     */
    public boolean hmSet(String key, Map<String, Object> map, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param by    要增加几(大于0)
     * @return      当前值
     */
    public double hIncrement(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key   键
     * @param item  项
     * @param by    要减少几(小于0)
     * @return      当前值
     */
    public double hDecrement(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================ set =============================
    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return    值
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key    键
     * @param value  值
     * @return       true-存在；false-不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key     键
     * @param values  值 可以是多个
     * @return        成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key     键
     * @param time    时间(秒)
     * @param values  值 可以是多个
     * @return        成功个数
     */
    public long sSetAndTime(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time, timeUnit);
            return count;
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key  键
     * @return     长度
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的缓存
     *
     * @param key     键
     * @param values  值，可以是多个
     * @return        移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }


    // ============================ zSet =============================
    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return    值
     */
    public Set<Object> zSGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key    键
     * @param value  值
     * @return       true-存在；false-不存在
     */
    public boolean zSHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key    键
     * @param value  值
     * @param score  分数
     * @return       true-成功；false-失败
     */
    public Boolean zSSet(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key     键
     * @param time    时间(秒)
     * @param values  值,可以是多个
     * @return        成功个数
     */
    public long zSSetAndTime(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time, timeUnit);
            return count;
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key  键
     * @return     缓存长度
     */
    public long zSGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的对象
     *
     * @param key     键
     * @param values  值，可以是多个
     * @return        移除的个数
     */
    public long zSetRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }


    // =============================== list =================================
    /**
     * 获取list缓存的内容
     *
     * @param key    键
     * @param start  开始 0 是第一个元素
     * @param end    结束 -1代表所有值
     * @return       缓存的内容
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return    缓存长度
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key    键
     * @param index  索引 index>=0时， 0-第0个元素，1-第1个元素，...；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return       元素值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param value  值
     * @return       结果
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param value  值
     * @param time   时间(秒)
     * @return       结果
     */
    public boolean lSet(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param value  值
     * @return       结果
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key     键
     * @param value   值
     * @param time    时间(秒)
     * @return        结果
     */
    public boolean lSet(String key, List<Object> value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key    键
     * @param index  索引
     * @param value  值
     * @return       结果
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error(key, e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key    键
     * @param count  移除多少个
     * @param value  值
     * @return       移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }


}
