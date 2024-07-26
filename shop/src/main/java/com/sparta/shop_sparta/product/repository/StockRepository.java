package com.sparta.shop_sparta.product.repository;

import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    Optional<StockEntity> findByProductEntity(ProductEntity productEntity);
    Optional<StockEntity> findByProductEntity_ProductId(Long productId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE `stock` SET amount = amount - :amount WHERE product_id = :productId", nativeQuery = true)
    void updateStockAfterOrder(@Param("productId") Long productId, @Param("amount") Long amount);
}
