package com.sparta.shop_sparta.domain.dto.order;

import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDetailRequestDto {
    private Long amount;
    private Long productId;
}
