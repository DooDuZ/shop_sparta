package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartDetailServiceImpl implements CartDetailService{
    @Override
    public List<CartDetailDto> getCartDetailsByCartEntity(CartEntity cartEntity) {
        return List.of();
    }
}
