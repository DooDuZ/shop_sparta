package com.sparta.shop_sparta.redis;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
public class RedisTest {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisTest(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Test
    @DisplayName("redis key - Set 구조 저장 test")
    public void addTest() {
        String key = "key";

        redisTemplate.delete(key);

        redisTemplate.opsForSet().add(key, "value");
        redisTemplate.opsForSet().add(key, "value2");
        redisTemplate.opsForSet().add(key, "PostmanRuntime/7.37.3");

        Set<Object> values = redisTemplate.opsForSet().members(key);

        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("redis key - Set 구조 데이터 삭제 test")
    public void removeTest() {
        String key = "key";

        redisTemplate.delete(key);

        redisTemplate.opsForSet().add(key, "value");
        redisTemplate.opsForSet().add(key, "value2");
        redisTemplate.opsForSet().add(key, "PostmanRuntime/7.37.3");

        redisTemplate.opsForSet().remove(key, "PostmanRuntime/7.37.3");

        redisTemplate.delete(key);
    }
}
