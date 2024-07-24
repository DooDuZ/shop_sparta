package com.sparta.shop_sparta.domain.dto.product;

import com.sparta.common.constant.product.ProductStatus;
import java.io.Serializable;
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
public class ReservationResponseDto implements Serializable {
    private Long reservationId;
    private Long productId;
    private LocalDateTime reservationTime;
    private ProductStatus reserveStatus;
}
