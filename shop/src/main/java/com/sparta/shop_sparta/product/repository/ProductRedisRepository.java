package com.sparta.shop_sparta.product.repository;

import com.sparta.shop_sparta.common.repository.RedisRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRedisRepository implements RedisRepository<String, Object> {

    private static final Logger log = LoggerFactory.getLogger(ProductRedisRepository.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final String prefix = "product : ";
    private final Duration timeout = Duration.ofMinutes(20);

    @Override
    public void saveWithDuration(String key, Object value) {
        String prefixKey = addPrefix(key);

        redisTemplate.opsForValue().set(prefixKey, value, timeout);
        redisTemplate.expire(prefixKey, timeout);
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(addPrefix(key), value, timeout);
    }

    @Override
    public Object find(String key) {
        return redisTemplate.opsForValue().get(addPrefix(key));
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(addPrefix(key));
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(addPrefix(key));
    }

    private String addPrefix(String key) {
        return prefix + key;
    }

    public void cache(String key, Object value){
        redisTemplate.opsForValue().setIfAbsent(addPrefix(key), value, timeout);
        redisTemplate.expire(addPrefix(key), timeout);
    }

    public void flushAll(){
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
