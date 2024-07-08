package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.StockRepository;
import com.sparta.shop_sparta.repository.memoryRepository.StockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{

    private final StockRepository stockRepository;
    private final StockRedisRepository stockRedisRepository;

    @Override
    public void addProduct(ProductEntity productEntity) {
        StockEntity stockEntity = StockEntity.builder().productEntity(productEntity).amount(0L).build();
        stockRepository.save(stockEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getStock(ProductEntity productEntity) {
        StockEntity stockEntity = getStockEntity(productEntity);
        return stockEntity.getAmount();
    }

    @Override
    public void updateStock(ProductEntity productEntity, Long amount) {
        getStockEntity(productEntity).setAmount(amount);
    }

    @Override
    public void updateStock(StockEntity stockEntity, Long amount) {
        stockEntity.setAmount(amount);
    }

    @Override
    public void deleteProduct(ProductEntity productEntity, Long amount) {
        getStockEntity(productEntity).setDelete(true);
    }

    @Override
    public StockEntity getStockByProductId(Long productId) {
        return stockRepository.findByProductEntity_ProductId(productId).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT.getMessage())
        );
    }

    public StockEntity getStockEntity(ProductEntity productEntity){
        return stockRepository.findByProductEntity(productEntity).orElseThrow(
                () -> new ProductException(ProductMessage.OUT_OF_STOCK.getMessage())
        );
    }

    @Override
    @Async
    public void updateStockAfterOrder(Long stockId, Long amount){
        stockRepository.updateStockAfterOrder(stockId, amount);
    }

    @Override
    public void redisCache(Long productId) {
        stockRedisRepository.saveWithDuration(String.valueOf(productId), getStockByProductId(productId).getAmount());
    }

    @Override
    public void updateStockInRedis(Long productId, Long amount) {
        String key = String.valueOf(productId);
        Integer stock = (Integer) stockRedisRepository.find(key);

        if(stock - amount < 0){
            throw new ProductException(ProductMessage.OUT_OF_STOCK.getMessage());
        }

        stockRedisRepository.save(key, stock - amount);
    }

    @Override
    public Boolean isCached(Long productId) {
        return stockRedisRepository.hasKey(String.valueOf(productId));
    }

    @Override
    public Long getStockInRedis(Long productId) {
        Integer stock = (Integer) stockRedisRepository.find(String.valueOf(productId));
        return stock.longValue();
    }
}