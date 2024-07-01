package com.sparta.shop_sparta.controller.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CartController {
    ResponseEntity<?> createCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto);
    ResponseEntity<?> getCart(UserDetails userDetails);
    ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long productId);
    ResponseEntity<?> updateCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto);
}
