package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProductService {
    ResponseEntity<?> createProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ResponseEntity<?> updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ResponseEntity<?> deleteProduct(UserDetails userDetails, Long productId);
    ProductDto getProduct(Long productId);
    ProductEntity getProductEntity(Long productId);
    ResponseEntity<?> getAllProducts();
    ResponseEntity<?> getAllProductsBySeller(Long sellerId);
    ResponseEntity<?> getAllByCategory(CategoryDto categoryDto);
    ProductDto getProductDto(ProductEntity productEntity);
    List<ProductDto> getProductDtoList(Map<Long, Long> cartInfo);
}
