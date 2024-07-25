package com.sparta.shop_sparta.product.domain.entity;

import com.sparta.common.constant.product.ProductStatus;
import com.sparta.shop_sparta.product.domain.dto.ReservationResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "reservation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus reserveStatus;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "productId")
    private ProductEntity productEntity;

    public void setReservationTime(LocalDateTime openDateTime) {
        this.reservationTime = openDateTime;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setReserveStatus(ProductStatus reserveStatus) {
        this.reserveStatus = reserveStatus;
    }

    public ReservationResponseDto toResponseDto(){
        return ReservationResponseDto.builder()
                .reservationId(this.getReservationId())
                .reservationTime(this.getReservationTime())
                .reserveStatus(this.getReserveStatus())
                .productId(productEntity.getProductId())
                .build();
    }
}
