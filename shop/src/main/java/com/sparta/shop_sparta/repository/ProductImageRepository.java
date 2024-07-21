package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    void deleteAllByProductEntity(ProductEntity productEntity);
    List<ProductImageEntity> findAllByProductEntity(ProductEntity productEntity);
    List<ProductImageEntity> findAllByProductEntityIn(List<ProductEntity> productEntities);

    @Query(value = "SELECT pi.*, p.image_version AS product_image_version " +
            "FROM product_image pi " +
            "JOIN product p ON pi.product_id = p.product_id " +
            "WHERE pi.image_version = p.image_version " +
            "AND p.product_id = :productId", nativeQuery = true)
    List<ProductImageEntity> findAllByProductAndImageVersion(Long productId);

    @Query(value = "SELECT pi.*, p.image_version AS product_image_version " +
            "FROM product_image pi " +
            "JOIN product p ON pi.product_id = p.product_id " +
            "WHERE pi.image_version = p.image_version " +
            "AND p.product_id IN (:productIds)", nativeQuery = true)
    List<ProductImageEntity> findAllByProductsAndImageVersion(List<Long> productIds);
}
