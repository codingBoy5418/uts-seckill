package org.uts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类,主要配置缓存的序列化、过期时间
 * */
@Configuration
public class RedisConfig {

    //@Value("${cache.defaultExpiration}")
    private long defaultExpiration;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        //Redis连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //key和hashKey采用String序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        //value和HashValue采用JSON序列化
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);

        return redisTemplate;
    }

    /**
     * 往容器中添加RedisCacheManager容器，并设置序列化方式,统一设置缓存的过期时间
     * @return RedisCacheManager
     */
    //@Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultExpiration))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    /*
      注入执行lua脚本的 RedisScript类
     */
    @Bean
    public RedisScript<Boolean> redisScript(){
        //返回值为Integer
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        //设置返回值
        script.setResultType(Boolean.class);
        //设置LUA脚本路径
        script.setLocation(new ClassPathResource("/lock/redis_lock_lua.lua"));
        return script;
    }

}
