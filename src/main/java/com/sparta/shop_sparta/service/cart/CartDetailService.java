package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import java.util.List;
import java.util.Map;

public interface CartDetailService {
    List<CartDetailResponseDto> getCartDetailsByCartEntity(CartEntity cartEntity);
    List<CartDetailResponseDto> mapToCartDetailDtoList(Map<Long, Long> cartDetailMap);
    void validateProduct(Long productId);
}
