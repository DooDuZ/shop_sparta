package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;

    // @Secured("ROLE_SELLER")
    @Override
    @PostMapping
    public ResponseEntity<?> createProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @ModelAttribute ProductRequestDto productRequestDto) {
        return productService.createProduct(userDetails, productRequestDto);
    }

    @Override
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @ModelAttribute ProductRequestDto productRequestDto) {
        return productService.updateProduct(userDetails, productRequestDto);
    }

    @Override
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long productId) {
        return null;
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    // 후에 페이징 적용
    @Override
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        return productService.getAllProducts();
    }

    @Override
    public ResponseEntity<?> getAllByCategory(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllProductsBySeller(Long sellerId) {
        return null;
    }
}
