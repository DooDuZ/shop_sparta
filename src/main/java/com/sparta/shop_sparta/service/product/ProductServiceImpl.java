package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.member.AuthorizationException;
import com.sparta.shop_sparta.exception.product.ProductException;
import com.sparta.shop_sparta.repository.CategoryRepository;
import com.sparta.shop_sparta.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ResponseEntity<?> addProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        System.out.println("productRequestDto = " + productRequestDto);
        System.out.println("files = " + productRequestDto.getProductThumbnails().size());
        System.out.println("files2 = " + productRequestDto.getProductDetailImages().size());

        ProductEntity productEntity = productRequestDto.toEntity();

        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_CATEGORY.getMessage())
        );

        productEntity.setProductStatus(ProductStatus.WAITING);
        productEntity.setCategoryEntity(categoryEntity);
        productEntity.setSellerEntity( (MemberEntity) userDetails );

        System.out.println("productEntity = " + productEntity);

        productRepository.save(productEntity);

        try {
            productImageService.addProductImages(productEntity, productRequestDto.getProductThumbnails(),
                    productRequestDto.getProductDetailImages());
        } catch (ProductException pe) {
            // deleteProduct(userDetails, productEntity.getProductId());
            return ResponseEntity.badRequest().body(pe.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteProduct(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT.getMessage())
        );

        // 래퍼 클래스에 == 쓰면 참조값을 비교한다. + or - 연산 시 자동 언박싱됨
        if (memberEntity.getMemberId() - productEntity.getSellerEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        productEntity.setDelete(true);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> getProduct(UserDetails userDetails, Long productId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllProducts(UserDetails userDetails) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllProductsBySeller(UserDetails userDetails, Long sellerId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllByCategory(UserDetails userDetails, CategoryDto categoryDto) {
        return null;
    }
}
