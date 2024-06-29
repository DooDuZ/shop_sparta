package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<?> addProduct(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute ProductRequestDto productRequestDto) {
        return productService.addProduct(userDetails, productRequestDto);
    }

    @Override
    public ResponseEntity<?> updateProduct(@AuthenticationPrincipal UserDetails userDetails,  ProductRequestDto productRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,  Long productId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getProduct(@AuthenticationPrincipal UserDetails userDetails,  Long productId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllProducts(UserDetails userDetails) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllByCategory(UserDetails userDetails, CategoryDto categoryDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllProductsBySeller(UserDetails userDetails, Long sellerId) {
        return null;
    }
}
