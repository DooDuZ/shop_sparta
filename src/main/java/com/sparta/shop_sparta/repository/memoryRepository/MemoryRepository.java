package com.sparta.shop_sparta.repository.memoryRepository;

public interface MemoryRepository<K, V> {
    void save(K key, V value);
    V find(K key);
    void deleteKey(K key);
}