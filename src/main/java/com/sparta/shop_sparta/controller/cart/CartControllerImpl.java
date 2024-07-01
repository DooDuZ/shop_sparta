package com.sparta.shop_sparta.controller.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailDto;
import com.sparta.shop_sparta.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartControllerImpl implements CartController{

    private final CartService cartService;

    @Override
    public ResponseEntity<?> createCartDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cartId, @RequestBody CartDetailDto cartDetailDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cartId) {
        return null;
    }

    @Override
    public ResponseEntity<?> removeCartDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cartDetailId) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateCartDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long cartDetailId, @RequestBody CartDetailDto cartDetailDto) {
        return null;
    }
}
