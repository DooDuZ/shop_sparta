package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.entity.cart.CartEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CartService {
    CartEntity createCart(MemberEntity memberEntity);
    ResponseEntity<?> createCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto);
    ResponseEntity<?> getCart(UserDetails userDetails);
    ResponseEntity<?> removeCartDetail(UserDetails userDetails, Long productId);
    ResponseEntity<?> updateCartDetail(UserDetails userDetails, CartDetailRequestDto cartDetailRequestDto);
    Map<Long, Long> getCartInRedis(MemberEntity memberEntity);
    void removeOrderedProduct(MemberEntity memberEntity, List<OrderDetailDto> orderedProductIds);
}
