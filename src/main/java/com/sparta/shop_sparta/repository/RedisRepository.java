package com.sparta.shop_sparta.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    public void save(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public void saveWithDuration(String key, Object value, Integer minute){
        Duration timeout = Duration.ofMinutes(minute);
        redisTemplate.opsForValue().set(key, value,timeout);
    }

    public Object find(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}
