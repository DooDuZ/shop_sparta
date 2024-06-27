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
        redisTemplate.opsForValue().set(addPrefix(key), value,timeout);
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForSet().add(addPrefix(key), value);
    }

    @Override
    public Object find(String key) {
        return redisTemplate.opsForValue().get(addPrefix(key));
    }

    public Boolean findUserAgent(String key, String userAgent) {
        return redisTemplate.opsForSet().isMember(addPrefix(key), userAgent);
    }

    public void deleteUserAgent(String key, String userAgent){
        redisTemplate.opsForSet().remove(addPrefix(key), userAgent);

        if (redisTemplate.opsForSet().members(addPrefix(key)).size() == 0){
            deleteKey(addPrefix(key));
        }
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(addPrefix(key));
    }

    private String addPrefix(String key){
        return prefix + key;
    }

    public int getSize(String key){
        return redisTemplate.opsForSet().members(addPrefix(key)).size();
    }
}
