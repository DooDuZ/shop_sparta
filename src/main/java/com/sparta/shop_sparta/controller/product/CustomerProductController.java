package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.service.product.CustomerProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class CustomerProductController {
    private final CustomerProductService customerProductService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(customerProductService.getProduct(productId));
    }

    // 후에 페이징 적용
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestParam int page, @RequestParam int itemsPerPage) {
        return ResponseEntity.ok(customerProductService.getAllProducts(page, itemsPerPage));
    }

    public ResponseEntity<?> getAllByCategory(CategoryDto categoryDto) {
        return ResponseEntity.ok(customerProductService.getAllByCategory(categoryDto));
    }

    public ResponseEntity<?> getAllProductsBySeller(Long sellerId) {
        return ResponseEntity.ok(customerProductService.getAllProductsBySeller(sellerId));
    }
}
