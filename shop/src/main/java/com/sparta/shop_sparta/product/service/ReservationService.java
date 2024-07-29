package com.sparta.shop_sparta.product.service;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.product.domain.dto.ReservationRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationResponseDto;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.ReservationEntity;
import com.sparta.shop_sparta.product.repository.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationResponseDto createReservation(ProductEntity productEntity, LocalDateTime reservationTime, Long reserveStatus) {
        if (reservationTime == null) {
            throw new ProductException(ProductMessage.INVALID_RESERVATION);
        }

        ReservationEntity reservationEntity = reservationRepository.save(
                ReservationEntity.builder()
                        .completed(false)
                        .reservationTime(reservationTime)
                        .reserveStatus(ProductStatus.of(reserveStatus))
                        .productEntity(productEntity)
                        .build()
        );

        return reservationEntity.toResponseDto();
    }

    @Transactional
    public ReservationResponseDto updateReservation(ReservationRequestDto reservationRequestDto) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationRequestDto.getReservationId()).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_RESERVATION)
        );

        reservationEntity.setReservationTime(reservationRequestDto.getReservationTime());
        reservationEntity.setReserveStatus(ProductStatus.of(reservationRequestDto.getReservationStatus()));

        return reservationEntity.toResponseDto();
    }

    @Transactional
    public List<ReservationResponseDto> updateReservations(Long productId, List<ReservationRequestDto> reservations) {
        List<ReservationResponseDto> updates = new ArrayList<>();

        for (ReservationRequestDto reservation : reservations) {

            if(productId - reservation.getProductId() != 0){
                throw new ProductException(ProductMessage.INVALID_RESERVATION);
            }

            ReservationEntity reservationEntity = reservationRepository.findById(reservation.getReservationId()).orElseThrow(
                    () -> new ProductException(ProductMessage.INVALID_RESERVATION)
            );

            reservationEntity.setReservationTime(reservation.getReservationTime());
            reservationEntity.setReserveStatus(ProductStatus.of(reservation.getReservationStatus()));

            updates.add(reservationEntity.toResponseDto());
        }

        return updates;
    }

    @Transactional
    public ReservationEntity cancelReservation(MemberEntity memberEntity, Long reservationId) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_RESERVATION)
        );

        if(memberEntity.getMemberId() - reservationEntity.getProductEntity().getSellerEntity().getMemberId() != 0){
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        reservationEntity.setCompleted(true);

        return reservationEntity;
    }

    @Transactional
    public void cancelProductReservation(ProductEntity productEntity) {
        List<ReservationEntity> reservationEntities = reservationRepository.findAllByProductEntity(productEntity);

        for (ReservationEntity reservationEntity : reservationEntities) {
            reservationEntity.setCompleted(true);
        }
    }

    public List<ReservationResponseDto> getReservationsByProductEntity(ProductEntity productEntity) {
        return reservationRepository.findByProductEntityAndCompletedFalse(productEntity)
                .stream()
                .map(ReservationEntity::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDto> getAllReservationsByProductEntities(List<ProductEntity> productEntities) {
        return reservationRepository.findAllByProductEntityInAndCompletedFalse(productEntities)
                .stream()
                .map(ReservationEntity::toResponseDto)
                .collect(Collectors.toList());
    }
}
