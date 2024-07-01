package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductResponseDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.CategoryRepository;
import com.sparta.shop_sparta.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    public ResponseEntity<?> createProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        ProductEntity productEntity = productRequestDto.toEntity();

        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_CATEGORY.getMessage())
        );

        productEntity.init(categoryEntity, (MemberEntity) userDetails);

        productRepository.save(productEntity);

        try {
            productImageService.createProductImages(productEntity, productRequestDto.getProductThumbnails(),
                    productRequestDto.getProductDetailImages());
        } catch (ProductException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(productRequestDto.getSellerId() - memberEntity.getMemberId() != 0){
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        ProductEntity productEntity = productRepository.findById(productRequestDto.getProductId()).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT.getMessage())
        );

        productEntity.update(productRequestDto);

        // [Todo] 이미지 update 적용
        // version 관리 방법 고민 후 적용

        return ResponseEntity.ok().build();
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
    public ResponseEntity<?> getProduct(Long productId) {
        ProductEntity productEntity = getProductEntity(productId);

        List<ProductImageDto> productImages =  productImageService.getProductImages(productEntity);
        ProductResponseDto productResponseDto = productEntity.toDto();
        productResponseDto.setProductImages(productImages);

        return ResponseEntity.ok(productResponseDto);
    }

    // 주문에서 사용할 수 있도록 분리
    @Override
    public ProductEntity getProductEntity(Long productId){
        return productRepository.findById(productId).orElseThrow(
                ()-> new ProductException(ProductMessage.NOT_FOUND_PRODUCT.getMessage())
        );
    }

    @Override
    public ProductResponseDto getProductResponseDto(Long productId){
        ProductEntity productEntity = getProductEntity(productId);

        List<ProductImageDto> productImages =  productImageService.getProductImages(productEntity);
        ProductResponseDto productResponseDto = productEntity.toDto();
        productResponseDto.setProductImages(productImages);

        return productResponseDto;
    }

    // 후에 페이징 처리 할 것
    // 다 때려박으면 이미지 용량 어쩔 건데...
    @Override
    public ResponseEntity<?> getAllProducts() {
        Map<Long, ProductResponseDto> productDtoList = productRepository.findAll().stream().map(ProductEntity::toDto).collect(
                Collectors.toMap(ProductResponseDto::getProductId, Function.identity()));

        List<ProductImageDto> productImageDtoList = productImageService.getAllProductImages();

        for (ProductImageDto productImageDto : productImageDtoList) {
            productDtoList.get(productImageDto.getProductId()).getProductImages().add(productImageDto);
        }

        return ResponseEntity.ok(new ArrayList<>(productDtoList.values()));
    }

    @Override
    public ResponseEntity<?> getAllProductsBySeller(Long sellerId) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllByCategory(CategoryDto categoryDto) {
        return null;
    }
}
