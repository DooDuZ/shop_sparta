package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import java.util.List;

public interface CartDetailService {
    List<CartDetailDto> getCartDetailsByCartEntity(CartEntity cartEntity);
}
