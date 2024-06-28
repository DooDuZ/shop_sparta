package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.item.ProductDto;
import com.sparta.shop_sparta.domain.dto.item.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {


    @Override
    public ResponseEntity<?> addProduct(@AuthenticationPrincipal UserDetails userDetails, ProductDto productDto) {

        return null;
    }

    @Override
    public ResponseEntity<?> updateProduct(@AuthenticationPrincipal UserDetails userDetails, ProductDto productDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal UserDetails userDetails, ProductDto productDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getProduct(@AuthenticationPrincipal UserDetails userDetails, ProductDto productDto) {
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
}
