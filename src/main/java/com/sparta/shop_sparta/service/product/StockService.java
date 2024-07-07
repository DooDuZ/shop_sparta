package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;

public interface StockService {
    void addProduct(ProductEntity productEntity);
    Long getStock(ProductEntity productEntity);
    void updateStock(ProductEntity productEntity, Long amount);
    void updateStock(StockEntity stockEntity, Long amount);
    void deleteProduct(ProductEntity productEntity, Long amount);
    StockEntity getStockByProductId(Long productId);
    StockEntity getStockEntity(ProductEntity productEntity);
    public void updateStockAfterOrder(Long stockId, Long amount);
}
