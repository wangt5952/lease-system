package com.elextec.framework.plugins.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

/**
 * Redis工具类.
 * Created by wangtao on 2018/1/16.
 */
@Component
public class RedisClient {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate<String, Object> redisTemplate() {
        return redisTemplate;
    }

    public HashOperations<String, String, Object> hashOperations() {
        return redisTemplate.opsForHash();
    }

    public ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }

    public ListOperations<String, Object> listOperations() {
        return redisTemplate.opsForList();
    }

    public SetOperations<String, Object> setOperations() {
        return redisTemplate.opsForSet();
    }

    public ZSetOperations<String, Object> zsetOperations() {
        return redisTemplate.opsForZSet();
    }
}
