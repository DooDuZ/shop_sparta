package com.sparta.shop_sparta.controller.cart;

import com.sparta.shop_sparta.domain.dto.cart.CartRequestDto;
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
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> createCartDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartRequestDto cartRequestDto) {
        return ResponseEntity.ok(cartService.addProductToCart(userDetails, cartRequestDto));
    }

    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails userDetails) {
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
