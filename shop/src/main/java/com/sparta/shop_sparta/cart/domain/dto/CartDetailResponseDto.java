package com.sparta.shop_sparta.cart.domain.dto;

import com.sparta.shop_sparta.product.domain.dto.ProductDto;
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
    private ProductDto productDto;
}
