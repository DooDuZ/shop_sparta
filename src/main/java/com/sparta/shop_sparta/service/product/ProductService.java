package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProductService {
    ResponseEntity<?> addProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ResponseEntity<?> updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ResponseEntity<?> deleteProduct(UserDetails userDetails, Long productId);
    ResponseEntity<?> getProduct(UserDetails userDetails, Long productId);
    ResponseEntity<?> getAllProducts(UserDetails userDetails);
    ResponseEntity<?> getAllProductsBySeller(UserDetails userDetails, Long sellerId);
    ResponseEntity<?> getAllByCategory(UserDetails userDetails, CategoryDto categoryDto);
}
