package com.sparta.shop_sparta.repository;

import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ReservationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByProductEntity(ProductEntity productEntity);
}
