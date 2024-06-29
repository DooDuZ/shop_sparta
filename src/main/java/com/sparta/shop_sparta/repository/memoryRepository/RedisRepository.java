package com.sparta.shop_sparta.repository.memoryRepository;

public interface RedisRepository<K, V> extends MemoryRepository<K, V>{
    void saveWithDuration(K key, V value, Integer minute);
}
