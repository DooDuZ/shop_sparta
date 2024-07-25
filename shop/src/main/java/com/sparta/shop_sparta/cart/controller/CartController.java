package com.sparta.shop_sparta.cart.controller;

import com.sparta.shop_sparta.cart.domain.dto.CartDto;
import com.sparta.shop_sparta.cart.domain.dto.CartRequestDto;
import com.sparta.shop_sparta.cart.service.CartService;
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
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> createCartDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartRequestDto cartRequestDto) {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeCartDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId) {
        cartService.removeCartDetail(userDetails, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateCartDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartRequestDto cartRequestDto) {
        cartService.updateCartDetail(userDetails, cartRequestDto);
        return ResponseEntity.ok().build();
    }
}
