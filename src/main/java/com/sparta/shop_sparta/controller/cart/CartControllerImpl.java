package com.sparta.shop_sparta.controller.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartDetailRequestDto;
import com.sparta.shop_sparta.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartControllerImpl implements CartController{

    private final CartService cartService;

    @Override
    @PostMapping
    public ResponseEntity<?> createCartDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartDetailRequestDto cartDetailRequestDto) {
        return cartService.createCartDetail(userDetails, cartDetailRequestDto);
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.getCart(userDetails);
    }

    @Override
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeCartDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId) {
        return cartService.removeCartDetail(userDetails, productId);
    }

    @Override
    @PutMapping
    public ResponseEntity<?> updateCartDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartDetailRequestDto cartDetailRequestDto) {
        return cartService.updateCartDetail(userDetails, cartDetailRequestDto);
    }
}
