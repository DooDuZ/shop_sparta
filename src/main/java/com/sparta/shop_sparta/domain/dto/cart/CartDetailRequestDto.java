package com.sparta.shop_sparta.domain.dto.cart;

import com.sparta.shop_sparta.domain.entity.cart.CartDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetailRequestDto {
    private Long amount;
    private Long productId;

    public CartDetailEntity toEntity(){
        return CartDetailEntity.builder().amount(this.amount).build();
    }
}
