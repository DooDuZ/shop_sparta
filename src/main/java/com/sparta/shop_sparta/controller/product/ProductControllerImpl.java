package com.sparta.shop_sparta.controller.product;

import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.service.product.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;

    // @Secured("ROLE_SELLER")
    @Override
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@AuthenticationPrincipal UserDetails userDetails,
                                                    @ModelAttribute ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(productService.createProduct(userDetails, productRequestDto));
    }

    @Override
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @ModelAttribute ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(productService.updateProduct(userDetails, productRequestDto));
    }

    @Override
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long productId) {
        productService.deleteProduct(userDetails, productId);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    // 후에 페이징 적용
    @Override
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(@RequestParam int page, @RequestParam int itemsPerPage) {
        return ResponseEntity.ok(productService.getAllProducts(page, itemsPerPage));
    }

    @Override
    public ResponseEntity<?> getAllByCategory(CategoryDto categoryDto) {
        return ResponseEntity.ok(productService.getAllByCategory(categoryDto));
    }

    @Override
    public ResponseEntity<?> getAllProductsBySeller(Long sellerId) {
        return ResponseEntity.ok(productService.getAllProductsBySeller(sellerId));
    }

    @Override
    @PatchMapping("/status")
    public ResponseEntity<Void> updateProductStatus(@RequestParam("product-id") Long productId, @RequestParam("status-code") Long productStatusCode) {
        productService.updateProductStatus(productId, productStatusCode);
        return ResponseEntity.ok().build();
    }
}
