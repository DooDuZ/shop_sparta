package com.sparta.shop_sparta.domain.dto.cart;

import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.constant.cart.CartStatus;
import java.util.ArrayList;
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
    private Long cartId;
    private CartStatus cartStatus;
    private Long memberId;

    private List<CartDetailDto> cartDetails;

    public CartEntity toEntity(){
        return CartEntity.builder().cartId(this.cartId).cartStatus(this.cartStatus).build();
    }

    public void setCartStatus(CartStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public void setCartDetails(List<CartDetailDto> cartDetails) {
        this.cartDetails = cartDetails;
    }
}
