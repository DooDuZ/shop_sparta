package com.sparta.shop_sparta.product.repository;

import com.sparta.common.constant.product.ProductStatus;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findBySellerEntity_MemberId(Long memberId);

    Page<ProductEntity> findAllByIsDeletedFalse(Pageable pageable);
    Page<ProductEntity> findAllByProductStatusAndIsDeletedFalse(Pageable pageable, ProductStatus productStatus);
    Page<ProductEntity> findAllByCategoryEntity_CategoryIdAndProductStatusAndIsDeletedFalse(Pageable pageable, Long categoryId, ProductStatus productStatus);
    Page<ProductEntity> findAllBySellerEntity_memberIdAndProductStatusAndIsDeletedFalse(Pageable pageable, Long memberId, ProductStatus productStatus);
    Page<ProductEntity> findAllBySellerEntityAndIsDeletedFalse(Pageable pageable, MemberEntity sellerEntity);
}
