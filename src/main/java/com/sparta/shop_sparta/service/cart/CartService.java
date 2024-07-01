package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CartService {
    CartDto createCart(MemberEntity memberEntity);
    ResponseEntity<?> createCartDetail(UserDetails userDetails, Long cartId, CartDetailDto cartDetailDto);
    ResponseEntity<?> getCart(UserDetails userDetails);
    ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long cartDetailId);
    ResponseEntity<?> updateCartDetail(UserDetails userDetails, Long cartDetailId, CartDetailDto cartDetailDto);
}
