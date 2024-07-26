package com.sparta.shop_sparta.cart.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long memberId;
    private List<CartDetailResponseDto> cartInfo;

    public void setCartDetails(List<CartDetailResponseDto> cartDetails) {
        this.cartInfo = cartDetails;
    }
}
