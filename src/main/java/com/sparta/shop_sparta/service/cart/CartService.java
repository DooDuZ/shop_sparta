package com.sparta.shop_sparta.service.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.dto.cart.CartRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CartService {
    void createCart(MemberEntity memberEntity);
    ProductDto addProductToCart(UserDetails userDetails, CartRequestDto cartRequestDto);
    CartDto getCart(UserDetails userDetails);
    void removeCartDetail(UserDetails userDetails, Long productId);
    void updateCartDetail(UserDetails userDetails, CartRequestDto cartRequestDto);
    void removeOrderedProduct(MemberEntity memberEntity, List<OrderDetailDto> orderedProductIds);
    Map<Long, Long> getCartInfo(UserDetails userDetails);
}
