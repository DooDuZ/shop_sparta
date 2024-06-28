package com.sparta.shop_sparta.repository.memoryRepository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtRedisRepository implements RedisRepository<String, Object> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String prefix = "refresh-code : ";

    @Override
    public void saveWithDuration(String key, Object value, Integer minute) {
        Duration timeout = Duration.ofMinutes(minute);
        redisTemplate.opsForValue().set(addPrefix(key), value, timeout);
        redisTemplate.expire(addPrefix(key), timeout);
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForSet().add(addPrefix(key), value);
    }

    @Override
    public Object find(String key) {
        return redisTemplate.opsForHash().entries(addPrefix(key));
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(addPrefix(key));
    }

    // set과 hashmap 사용 시 로직이 같음
    public void deleteUserAgent(String key, String userAgent) {
        redisTemplate.opsForHash().delete(addPrefix(key), userAgent);

        if (redisTemplate.opsForHash().entries(addPrefix(key)).size() == 0) {
            deleteKey(addPrefix(key));
        }
    }

    // hash 사용을 위한 overloading
    public void saveWithDuration(String key, String userAgent, String refreshToken, Integer minute) {
        Duration timeout = Duration.ofMinutes(minute);
        redisTemplate.opsForHash().put(addPrefix(key), userAgent, refreshToken);
        redisTemplate.expire(addPrefix(key), timeout);
    }

    public void save(String key, String userAgent, String refreshToken){
        redisTemplate.opsForHash().put(addPrefix(key), userAgent, refreshToken);
    }

    public String findUserAgent(String key, String userAgent){
        return (String) redisTemplate.opsForHash().get(addPrefix(key), userAgent);
    }

    public Boolean isUserAgent(String key, String userAgent) {
        return redisTemplate.opsForHash().hasKey(addPrefix(key), userAgent);
    }
    // hash 사용을 위한 overloading

    private String addPrefix(String key) {
        return prefix + key;
    }

    public int getSize(String key) {
        return redisTemplate.opsForHash().size(addPrefix(key)).intValue();
    }
}
