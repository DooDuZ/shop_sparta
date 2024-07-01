package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CartService {
    CartDto createCart(MemberEntity memberEntity);
    ResponseEntity<?> createCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto);
    ResponseEntity<?> getCart(UserDetails userDetails);
    ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long productId);
    ResponseEntity<?> updateCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto);
}
