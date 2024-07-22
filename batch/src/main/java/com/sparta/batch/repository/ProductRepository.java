package com.sparta.batch.repository;

import com.sparta.batch.domain.entity.product.ProductEntity;
import com.sparta.common.constant.product.ProductStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE `product` SET `productStatus` = :productStatus WHERE `productStatus` = :prevStatus", nativeQuery = true)
    void openAllProductStatus(@Param("prevStatus") ProductStatus prevStatus, @Param("productStatus") ProductStatus productStatus);
}
