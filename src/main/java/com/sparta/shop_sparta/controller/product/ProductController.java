package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationResponseDto;
import com.sparta.shop_sparta.domain.dto.product.StockRequestDto;
import com.sparta.shop_sparta.service.product.ProductService;
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
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // @Secured("ROLE_SELLER")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                    @ModelAttribute ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(productService.createProduct(userDetails, productRequestDto));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @ModelAttribute ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(productService.updateProduct(userDetails, productRequestDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long productId) {
        productService.deleteProduct(userDetails, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    // 후에 페이징 적용
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestParam int page, @RequestParam int itemsPerPage) {
        return ResponseEntity.ok(productService.getAllProducts(page, itemsPerPage));
    }

    public ResponseEntity<?> getAllByCategory(CategoryDto categoryDto) {
        return ResponseEntity.ok(productService.getAllByCategory(categoryDto));
    }

    public ResponseEntity<?> getAllProductsBySeller(Long sellerId) {
        return ResponseEntity.ok(productService.getAllProductsBySeller(sellerId));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateProductStatus(@RequestParam("product-id") Long productId, @RequestParam("status-code") Long productStatusCode) {
        productService.updateProductStatus(productId, productStatusCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponseDto> createReservation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(productService.createReservation(userDetails, reservationRequestDto));
    }

    @PutMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationResponseDto> updateReservation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.ok(productService.updateReservation(userDetails, reservationRequestDto));
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long reservationId) {
        productService.cancelReservation(userDetails, reservationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/stock")
    public ResponseEntity<?> updateStock(@AuthenticationPrincipal UserDetails userDetails, @RequestBody StockRequestDto stockRequestDto) {
        return ResponseEntity.ok(productService.updateStock(userDetails, stockRequestDto));
    }
}
