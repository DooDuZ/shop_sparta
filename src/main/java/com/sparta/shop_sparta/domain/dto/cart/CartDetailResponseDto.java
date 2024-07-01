package com.sparta.shop_sparta.domain.dto.cart;

import com.sparta.shop_sparta.domain.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetailResponseDto {
    private Long amount;
    private ProductResponseDto productResponseDto;
}
