package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.ReservationRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationResponseDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ReservationEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.ReservationRepository;
import java.util.List;
import java.util.Optional;
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
    public ReservationResponseDto createReservation(ProductEntity productEntity, LocalDateTime reservationTime, ProductStatus reserveStatus) {
        ReservationEntity reservationEntity = reservationRepository.save(
                ReservationEntity.builder()
                        .completed(false)
                        .reservationTime(reservationTime)
                        .reserveStatus(reserveStatus)
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
        reservationEntity.setReserveStatus(ProductStatus.of(reservationRequestDto.getReserveStatus()));

        return reservationEntity.toResponseDto();
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
