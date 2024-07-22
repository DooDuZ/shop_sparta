package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationResponseDto;
import com.sparta.shop_sparta.domain.dto.product.StockRequestDto;
import com.sparta.shop_sparta.service.product.SellerProductService;
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

    @GetMapping("/product-status")
    public ResponseEntity<List<ProductDto>> getSellerProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int page,
            @RequestParam int itemsPerPage,
            @RequestParam("product-status") Long productStatus
    ) {
        return ResponseEntity.ok(sellerProductService.getSellerProducts(userDetails, page, itemsPerPage, productStatus));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                    @ModelAttribute ProductRequestDto productRequestDto) {
        System.out.println("들어옴1");
        return ResponseEntity.ok(sellerProductService.updateProduct(userDetails, productRequestDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long productId) {
        sellerProductService.deleteProduct(userDetails, productId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateProductStatus(@RequestParam("product-id") Long productId,
                                                    @RequestParam("status-code") Long productStatusCode) {
        sellerProductService.updateProductStatus(productId, productStatusCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponseDto> createReservation(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(sellerProductService.createReservation(userDetails, reservationRequestDto));
    }

    @PutMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationResponseDto> updateReservation(@AuthenticationPrincipal UserDetails userDetails,
                                                                    @RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(sellerProductService.updateReservation(userDetails, reservationRequestDto));
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long reservationId) {
        sellerProductService.cancelReservation(userDetails, reservationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/stock")
    public ResponseEntity<?> updateStock(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody StockRequestDto stockRequestDto) {
        return ResponseEntity.ok(sellerProductService.updateStock(userDetails, stockRequestDto));
    }
}
