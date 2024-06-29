package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProductImageController {
    ResponseEntity<?> addProductImages(UserDetails userDetails, Long productId, List<ProductImageDto> productImages);
    ResponseEntity<?> getProductImages(UserDetails userDetails, Long productId);
    ResponseEntity<?> updateProductImages(UserDetails userDetails, Long productId ,List<ProductImageDto> productImages);
    ResponseEntity<?> deleteProductImages(UserDetails userDetails, Long productId ,List<ProductImageDto> productImages);
}
