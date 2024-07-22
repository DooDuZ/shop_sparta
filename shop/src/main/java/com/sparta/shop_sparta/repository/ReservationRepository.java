package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ReservationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByProductEntityAndCompletedFalse(ProductEntity productEntity);
    List<ReservationEntity> findAllByProductEntity(ProductEntity productEntity);
    List<ReservationEntity> findAllByProductEntityInAndCompletedFalse(List<ProductEntity> productEntities);
}
