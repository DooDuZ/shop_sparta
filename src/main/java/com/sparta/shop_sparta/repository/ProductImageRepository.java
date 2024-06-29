package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
}
