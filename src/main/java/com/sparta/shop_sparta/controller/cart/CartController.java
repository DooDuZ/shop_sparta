package com.sparta.shop_sparta.controller.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CartController {
    ResponseEntity<?> createCartDetail(UserDetails userDetails, Long cartId, CartDetailDto cartDetailDto);
    ResponseEntity<?> getCart(UserDetails userDetails, Long cartId);
    ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long cartDetailId);
    ResponseEntity<?> updateCartDetail(UserDetails userDetails, Long cartDetailId, CartDetailDto cartDetailDto);
}
