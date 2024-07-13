package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.StockRepository;
import com.sparta.shop_sparta.repository.memoryRepository.StockRedisRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;*/

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockRedisRepository stockRedisRepository;
    //private final RedissonClient redissonClient;

    @Override
    public void addProduct(ProductEntity productEntity, Long amount) {
        StockEntity stockEntity = StockEntity.builder().productEntity(productEntity).amount(amount).build();
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
        String key = String.valueOf(productEntity.getProductId());

        if (stockRedisRepository.hasKey(key)){
            stockRedisRepository.deleteKey(key);
        }

        getStockEntity(productEntity).setAmount(amount);
    }

    @Override
    public void updateStock(StockEntity stockEntity, Long amount) {
        String key = String.valueOf(stockEntity.getProductEntity().getProductId());

        if (stockRedisRepository.hasKey(key)){
            stockRedisRepository.deleteKey(key);
        }

        stockEntity.setAmount(amount);
    }

    @Override
    public void deleteProduct(ProductEntity productEntity, Long amount) {
        getStockEntity(productEntity).setDelete(true);
    }

    @Override
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

    @Override
    @Async
    @Transactional
    public void updateStockAfterOrder(Long stockId, Long amount){
        stockRepository.updateStockAfterOrder(stockId, amount);
    }

    @Override
    public void redisCache(Long productId) {
        String key = String.valueOf(productId);
        StockEntity stockEntity = getStockByProductId(productId);

        if(stockRedisRepository.hasKey(key)){
            return;
        }

        stockRedisRepository.saveWithDuration(String.valueOf(productId), stockEntity.getAmount());
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
    @Override
    public void updateStockInRedis(Long productId, Long amount) {
        String key = String.valueOf(productId);
        stockRedisRepository.increment(key, -amount);
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

    @Override
    @Async
    public void repairStock(OrderDetailEntity orderDetailEntity) {
        StockEntity stockEntity = getStockEntity(orderDetailEntity.getProductEntity());
        stockRepository.updateStockAfterOrder(stockEntity.getStockId(), -orderDetailEntity.getAmount());
    }
}
