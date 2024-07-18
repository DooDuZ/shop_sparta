package com.sparta.shop_sparta.domain.dto.product;

import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.entity.product.ReservationEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class ReservationResponseDto {
    private Long reservationId;
    private Long productId;
    private LocalDateTime reservationTime;
    private ProductStatus reserveStatus;
}
