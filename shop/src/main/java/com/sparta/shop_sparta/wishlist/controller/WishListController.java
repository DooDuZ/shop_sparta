package com.sparta.shop_sparta.wishlist.controller;

import com.sparta.shop_sparta.wishlist.domain.dto.WishListRequestDto;
import com.sparta.shop_sparta.wishlist.domain.dto.WishListResponseDto;
import com.sparta.shop_sparta.wishlist.service.WishListService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/wishlist"))
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;

    @PostMapping
    public ResponseEntity<WishListResponseDto> addWishList(@AuthenticationPrincipal UserDetails userDetails, @RequestBody WishListRequestDto wishListRequestDto) {
        return ResponseEntity.ok(wishListService.addWishList(userDetails, wishListRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<WishListResponseDto>> getWishList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int page,
            @RequestParam("item-per-page") int itemsPerPage
    ) {
        return ResponseEntity.ok(wishListService.getWishList(userDetails, page, itemsPerPage));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteWishList(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("product-id") Long productId) {
        wishListService.deleteWishList(userDetails, productId);
        return ResponseEntity.ok().build();
    }
}
