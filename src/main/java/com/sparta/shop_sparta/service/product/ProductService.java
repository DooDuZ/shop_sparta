package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.item.CategoryDto;
import com.sparta.shop_sparta.domain.dto.item.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProductService {
    ResponseEntity<?> addProduct(UserDetails userDetails, ProductDto productDto);
    ResponseEntity<?> updateProduct(UserDetails userDetails, ProductDto productDto);
    ResponseEntity<?> deleteProduct(UserDetails userDetails, ProductDto productDto);
    ResponseEntity<?> getProduct(UserDetails userDetails, ProductDto productDto);
    ResponseEntity<?> getAllProducts(UserDetails userDetails);
    ResponseEntity<?> getAllByCategory(UserDetails userDetails, CategoryDto categoryDto);
}
