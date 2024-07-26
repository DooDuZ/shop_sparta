package com.sparta.shop_sparta.product.controller;

import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.service.CustomerProductService;
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

    @GetMapping("/status/all")
    public ResponseEntity<List<ProductDto>> getAllProducts(
            @RequestParam int page,
            @RequestParam("item-per-page") int itemsPerPage,
            @RequestParam("product-status") long productStatus
    ) {
        return ResponseEntity.ok(customerProductService.getAllProductsByProductStatus(page, itemsPerPage, productStatus));
    }

    @GetMapping("/category/all")
    public ResponseEntity<List<ProductDto>> getAllByCategory(
            @RequestParam int page,
            @RequestParam("item-per-page") int itemsPerPage,
            @RequestParam("category-id") Long categoryId
    ) {
        return ResponseEntity.ok(customerProductService.getAllByCategory(page, itemsPerPage, categoryId));
    }

    @GetMapping("/by-seller/all")
    public ResponseEntity<List<ProductDto>> getAllProductsBySeller(
            @RequestParam int page,
            @RequestParam("item-per-page") int itemsPerPage,
            @RequestParam("seller-id") Long sellerId
    ) {
        return ResponseEntity.ok(customerProductService.getAllProductsBySeller(page, itemsPerPage, sellerId));
    }
}
