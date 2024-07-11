package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ReservationEntity;
import com.sparta.shop_sparta.repository.ReservationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationEntity createReservation(ProductEntity productEntity, LocalDateTime openDateTime) {
        return reservationRepository.save(
                ReservationEntity.builder().completed(false).openDateTime(openDateTime).productEntity(productEntity)
                        .build()
        );
    }

    @Transactional
    public ReservationEntity updateReservationTime(ProductEntity productEntity, LocalDateTime openDateTime) {
        ReservationEntity reservationEntity = reservationRepository.findByProductEntity(productEntity).orElseThrow(
                () -> new RuntimeException("reservation not found")
        );

        reservationEntity.setOpenDateTime(openDateTime);

        return reservationEntity;
    }

    @Transactional
    public ReservationEntity reservationCompleted(ProductEntity productEntity) {
        ReservationEntity reservationEntity = reservationRepository.findByProductEntity(productEntity).orElseThrow(
                () -> new RuntimeException("reservation not found")
        );

        reservationEntity.setCompleted(true);

        return reservationEntity;
    }
}
