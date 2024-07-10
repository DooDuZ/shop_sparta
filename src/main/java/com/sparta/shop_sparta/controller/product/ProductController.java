package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProductController {
    ResponseEntity<?> createProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ResponseEntity<?> updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ResponseEntity<?> deleteProduct(UserDetails userDetails, Long productId);
    ResponseEntity<?> getProduct(Long productId);
    ResponseEntity<?> getAllProducts(int page, int itemsPerPage);
    ResponseEntity<?> getAllByCategory(CategoryDto categoryDto);
    ResponseEntity<?> getAllProductsBySeller(Long sellerId);
    ResponseEntity<?> updateProductStatus(Long productId, Long productStatusCode);
}
