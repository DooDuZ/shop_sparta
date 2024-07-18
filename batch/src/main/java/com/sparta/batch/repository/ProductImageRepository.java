package com.sparta.batch.repository;

import com.sparta.batch.domain.entity.product.ProductEntity;
import com.sparta.batch.domain.entity.product.ProductImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    void deleteAllByProductEntity(ProductEntity productEntity);
    List<ProductImageEntity> findAllByProductEntity(ProductEntity productEntity);
    List<ProductImageEntity> findAllByProductEntityIn(List<ProductEntity> productEntities);
}
