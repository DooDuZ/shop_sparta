package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.entity.cart.CartDetailEntity;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import java.util.List;
import java.util.Map;

public interface CartDetailService {
    List<CartDetailResponseDto> getCartDetailResponseByCartEntity(CartEntity cartEntity);
    List<CartDetailResponseDto> mapToCartDetailDtoList(Map<Long, Long> cartDetailMap);
    List<CartDetailEntity> getCartDetailsByCartEntity(CartEntity cartEntity);
    void validateProduct(Long productId);
    void addCartDetail(CartEntity cartEntity, Map<Long, Long> cartInfo);
    void removeOrderedProduct(CartEntity cartEntity, Long productId);
    void removeCart(CartEntity cartEntity);
}
