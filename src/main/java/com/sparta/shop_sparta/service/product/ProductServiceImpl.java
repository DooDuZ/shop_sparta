package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.item.CategoryDto;
import com.sparta.shop_sparta.domain.dto.item.ProductDto;
import com.sparta.shop_sparta.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public ResponseEntity<?> addProduct(UserDetails userDetails, ProductDto productDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateProduct(UserDetails userDetails, ProductDto productDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteProduct(UserDetails userDetails, ProductDto productDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getProduct(UserDetails userDetails, ProductDto productDto) {
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
