package com.sparta.shop_sparta.product.controller;

import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.dto.ProductRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ProductStatusRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationResponseDto;
import com.sparta.shop_sparta.product.domain.dto.StockRequestDto;
import com.sparta.shop_sparta.product.domain.dto.StockResponseDto;
import com.sparta.shop_sparta.product.service.SellerProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/seller")
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductService sellerProductService;

    // @Secured("ROLE_SELLER")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ProductRequestDto productRequestDto
    ) {
        return ResponseEntity.ok(sellerProductService.createProduct(userDetails, productRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getSellerProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int page,
            @RequestParam("item-per-page") int itemsPerPage
    ) {
        return ResponseEntity.ok(sellerProductService.getSellerProducts(userDetails, page, itemsPerPage));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ProductRequestDto productRequestDto,
            @PathVariable("product-id") Long productId
    ) {
        return ResponseEntity.ok(sellerProductService.updateProduct(userDetails, productRequestDto, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long productId) {
        sellerProductService.deleteProduct(userDetails, productId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateProductStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductStatusRequestDto productStatusRequestDto
    ) {
        sellerProductService.updateProductStatus(userDetails, productStatusRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponseDto> createReservation(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(sellerProductService.createReservation(userDetails, reservationRequestDto));
    }

    @PutMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReservationRequestDto reservationRequestDto,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(sellerProductService.updateReservation(userDetails, reservationRequestDto, reservationId));
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long reservationId) {
        sellerProductService.cancelReservation(userDetails, reservationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/stock")
    public ResponseEntity<StockResponseDto> updateStock(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody StockRequestDto stockRequestDto) {
        return ResponseEntity.ok(sellerProductService.updateStock(userDetails, stockRequestDto));
    }
}
