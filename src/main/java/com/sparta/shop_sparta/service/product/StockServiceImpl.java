package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{

    private final StockRepository stockRepository;

    @Override
    public void addProduct(ProductEntity productEntity) {
        StockEntity stockEntity = StockEntity.builder().productEntity(productEntity).amount(0L).build();
        stockRepository.save(stockEntity);
    }

    @Override
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
    public void updateStockAfterOrder(Long stockId, Long amount){
        stockRepository.updateStockAfterOrder(stockId, amount);
    }
}
