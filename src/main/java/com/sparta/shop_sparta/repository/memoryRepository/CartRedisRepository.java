package com.sparta.shop_sparta.repository.memoryRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String prefix = "cart-code : ";
    private final Integer timeToLive = 60 * 24;

    public void saveWithDuration(Long key, Long productId, Long amount) {
        Duration timeout = Duration.ofMinutes(timeToLive);
        redisTemplate.opsForHash().put(addPrefix(key), productId, amount);
        redisTemplate.expire(addPrefix(key), timeout);
    }

    public void addKey(Long key){
        redisTemplate.opsForHash().putAll(addPrefix(key), new HashMap<>());
    }

    public void save(Long key, Long productId, Long amount){
        redisTemplate.opsForHash().put(addPrefix(key), productId, amount);
    }

    public Map<Long, Long> findCart(Long key){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(addPrefix(key));

        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> (Long) e.getKey(),
                        e -> (Long) e.getValue()
                ));
    }

    public Boolean hasKey(Long key){
        return redisTemplate.hasKey(addPrefix(key));
    }

    public void updateCartDetail(Long key, Long productId, Long amount){
        save(key, productId, amount);
    }

    public void removeCartDetail(Long key, Long productId){
        redisTemplate.opsForHash().delete(addPrefix(key), productId);
    }

    public void removeKey(Long key){
        redisTemplate.delete(addPrefix(key));
    }

    private String addPrefix(Long key) {
        return prefix + key;
    }

    public Set<Long> getAllCartKeys() {
        String pattern = prefix + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if(keys.isEmpty()){
            return Set.of();
        }

        return keys.stream()
                .map(key -> key.replace(prefix, ""))
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    public void deleteAllCartKeys() {
        String pattern = prefix + "*";
        Set<String> keys = redisTemplate.keys(pattern);

        if(keys.isEmpty()){
            return;
        }

        for(String key : keys){
            redisTemplate.delete(key);
        }
    }
}
