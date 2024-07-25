package com.sparta.shop_sparta.common.repository;

public interface RedisRepository<K, V> extends MemoryRepository<K, V>{
    void saveWithDuration(K key, V value);
}
