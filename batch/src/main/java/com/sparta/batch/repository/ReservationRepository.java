package com.sparta.batch.repository;


import com.sparta.batch.domain.entity.product.ProductEntity;
import com.sparta.batch.domain.entity.product.ReservationEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByProductEntity(ProductEntity productEntity);
    Page<ReservationEntity> findAllByCompletedFalseAndReservationTimeLessThanEqual(LocalDateTime dateTime, Pageable pageable);
}
