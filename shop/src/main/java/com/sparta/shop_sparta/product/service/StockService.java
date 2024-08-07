package com.sparta.shop_sparta.product.service;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.order.domain.entity.OrderDetailEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.product.repository.StockRepository;
import com.sparta.shop_sparta.product.repository.StockRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;*/

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final StockRepository stockRepository;
    private final StockRedisRepository stockRedisRepository;
    //private final RedissonClient redissonClient;

    public void addProduct(ProductEntity productEntity, Long amount) {
        StockEntity stockEntity = StockEntity.builder().productEntity(productEntity).amount(amount).build();
        stockRepository.save(stockEntity);
    }

    @Transactional(readOnly = true)
    public Long getStock(ProductEntity productEntity) {
        StockEntity stockEntity = getStockEntity(productEntity);
        return stockEntity.getAmount();
    }

    public StockEntity updateStock(ProductEntity productEntity, Long amount) {
        String key = String.valueOf(productEntity.getProductId());

        if (stockRedisRepository.hasKey(key)){
            stockRedisRepository.deleteKey(key);
        }

        StockEntity stockEntity = getStockEntity(productEntity);
        stockEntity.setAmount(amount);

        return stockEntity;
    }

    public void updateStock(StockEntity stockEntity, Long amount) {
        String key = String.valueOf(stockEntity.getProductEntity().getProductId());

        if (stockRedisRepository.hasKey(key)){
            stockRedisRepository.deleteKey(key);
        }

        stockEntity.setAmount(amount);
    }

    public void deleteProduct(ProductEntity productEntity, Long amount) {
        getStockEntity(productEntity).setDelete(true);
    }

    public StockEntity getStockByProductId(Long productId) {
        return stockRepository.findByProductEntity_ProductId(productId).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT)
        );
    }

    public StockEntity getStockEntity(ProductEntity productEntity){
        return stockRepository.findByProductEntity(productEntity).orElseThrow(
                () -> new ProductException(ProductMessage.OUT_OF_STOCK)
        );
    }

    @Async
    @Transactional
    public void updateStockAfterOrder(Long productId, Long amount){
        stockRepository.updateStockAfterOrder(productId, amount);
    }

    public void redisCache(Long productId) {
        String key = String.valueOf(productId);
        StockEntity stockEntity = getStockByProductId(productId);

        stockRedisRepository.cache(key, stockEntity.getAmount());
/*
        if(stockRedisRepository.hasKey(key)){
            return;
        }

        stockRedisRepository.saveWithDuration(String.valueOf(productId), stockEntity.getAmount());*/
    }

    /*@Override
    public void updateStockInRedis(Long productId, Long amount) {
        String key = String.valueOf(productId);

        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(10, TimeUnit.SECONDS);

            if (isLocked){
                Integer stock = (Integer) stockRedisRepository.find(key);

                if(stock - amount < 0){
                    throw new ProductException(ProductMessage.OUT_OF_STOCK);
                }

                stockRedisRepository.save(key, stock - amount);
            }
        }catch (InterruptedException e){
            throw new ProductException(ProductMessage.FAIL_TO_CONNECT);
        }finally {
            if(isLocked){
                lock.unlock();
            }
        }
    }*/
    public void updateStockInRedis(Long productId, Long amount) {
        String key = String.valueOf(productId);
        stockRedisRepository.increment(key, -amount);
    }

    public Boolean isCached(Long productId) {
        return stockRedisRepository.hasKey(String.valueOf(productId));
    }


    public Long getStockInRedis(Long productId) {
        Integer stock = (Integer) stockRedisRepository.find(String.valueOf(productId));
        return stock.longValue();
    }

    @Async
    public void repairStock(OrderDetailEntity orderDetailEntity) {
        stockRepository.updateStockAfterOrder(orderDetailEntity.getProductEntity().getProductId(), -orderDetailEntity.getAmount());
    }
}
