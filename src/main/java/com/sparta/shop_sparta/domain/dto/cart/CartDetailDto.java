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
public class CartDetailDto {
    private Long cartDetailId;
    private Integer amount;

    private Long itemId;
    private Long cartId;

    public CartDetailEntity toEntity(){
        return CartDetailEntity.builder().cartDetailId(this.cartDetailId).amount(this.amount).build();
    }
}
