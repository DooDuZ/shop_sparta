package com.sparta.shop_sparta.repository;

import com.sparta.common.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findBySellerEntity_MemberId(Long memberId);

    Page<ProductEntity> findAll(Pageable pageable);
    Page<ProductEntity> findAllByProductStatus(Pageable pageable, ProductStatus productStatus);
    Page<ProductEntity> findAllByCategoryEntity_CategoryIdAndProductStatus(Pageable pageable, Long categoryId, ProductStatus productStatus);
    Page<ProductEntity> findAllBySellerEntity_memberIdAndProductStatus(Pageable pageable, Long memberId, ProductStatus productStatus);
    Page<ProductEntity> findAllBySellerEntity(Pageable pageable, MemberEntity sellerEntity);
}
