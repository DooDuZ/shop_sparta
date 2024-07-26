package com.sparta.shop_sparta.member.repository;

import com.sparta.shop_sparta.common.repository.RedisRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SignupVerifyCodeRedisRepository implements RedisRepository<String, Object> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String prefix = "signup-code : ";
    private final Duration timeout = Duration.ofMinutes(10);

    @Override
    public void saveWithDuration(String key, Object value) {
        redisTemplate.opsForValue().set(addPrefix(key), value, timeout);
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
    public void deleteKey(String key) {
        redisTemplate.delete(addPrefix(key));
    }

    private String addPrefix(String key){
        return prefix + key;
    }
}
