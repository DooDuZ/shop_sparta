package com.sparta.shop_sparta.repository.memoryRepository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SignupVerifyCodeRedisRepository implements RedisRepository<String, Object>{

    private final RedisTemplate<String, Object> redisTemplate;
    private final String prefix = "signup-code";

    @Override
    public void saveWithDuration(String key, Object value, Integer minute) {
        Duration timeout = Duration.ofMinutes(minute);
        redisTemplate.opsForValue().set(key, value,timeout);
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(addPrefix(key), value);
    }

    @Override
    public Object find(String key) {
        return redisTemplate.opsForValue().get(addPrefix(key));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(addPrefix(key));
    }

    private String addPrefix(String key){
        return prefix + key;
    }
}
