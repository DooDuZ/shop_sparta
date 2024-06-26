package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findBySellerEntity_MemberId(Long memberId);
}
