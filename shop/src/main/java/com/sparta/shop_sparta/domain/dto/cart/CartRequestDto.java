package com.sparta.shop_sparta.domain.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDto {
    private Long amount;
    private Long productId;
}
