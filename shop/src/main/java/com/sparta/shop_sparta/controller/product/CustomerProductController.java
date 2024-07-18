package com.sparta.shop_sparta.controller.product;

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

    @GetMapping("/on-sale/all")
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestParam int page, @RequestParam int itemsPerPage, @RequestParam long productStatus) {
        return ResponseEntity.ok(customerProductService.getAllProductsByProductStatus(page, itemsPerPage, productStatus));
    }

    @GetMapping("/on-sale/category")
    public ResponseEntity<?> getAllByCategory(@RequestParam int page, @RequestParam int itemsPerPage, @RequestParam Long categoryId) {
        return ResponseEntity.ok(customerProductService.getAllByCategory(page, itemsPerPage, categoryId));
    }

    @GetMapping("/on-sale/seller")
    public ResponseEntity<?> getAllProductsBySeller(@RequestParam int page, @RequestParam int itemsPerPage, @RequestParam Long sellerId) {
        return ResponseEntity.ok(customerProductService.getAllProductsBySeller(page, itemsPerPage, sellerId));
    }
}
