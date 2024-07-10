package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.CategoryRepository;
import com.sparta.shop_sparta.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockService stockService;

    @Override
    @Transactional
    public ProductDto createProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        ProductEntity productEntity = productRequestDto.toEntity();

        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_CATEGORY.getMessage())
        );

        productEntity.init(categoryEntity, (MemberEntity) userDetails);

        ProductEntity product = productRepository.save(productEntity);

        stockService.addProduct(productEntity);
        stockService.updateStock(productEntity, productRequestDto.getAmount());

        productImageService.createProductImages(productEntity, productRequestDto.getProductThumbnails(),
                productRequestDto.getProductDetailImages());

        return product.toDto();
    }

    @Override
    @Transactional
    public ProductDto updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        productEntity.update(productRequestDto);
        stockService.updateStock(productEntity, productRequestDto.getAmount());
        //StockEntity stockEntity = stockService.getStockEntity(productEntity);
        //stockEntity.setAmount(productRequestDto.getAmount());

        // [Todo] 이미지 update 적용
        // version 관리 방법 고민 후 적용

        // Todo 상품 재고 업데이트 시 redis caching 다시하기 or 기존 캐싱 데이터 삭제

        return productEntity.toDto();
    }

    @Override
    public void deleteProduct(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productId);

        // 래퍼 클래스에 == 쓰면 참조값을 비교한다. + or - 연산 시 자동 언박싱됨
        if (memberEntity.getMemberId() - productEntity.getSellerEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        productEntity.setDelete(true);
    }

    @Override
    public ProductDto getProduct(Long productId) {
        ProductEntity productEntity = getProductEntity(productId);
        return getProductDto(productEntity);
    }

    // 주문에서 사용할 수 있도록 분리
    @Override
    public ProductEntity getProductEntity(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT.getMessage())
        );
    }

    public List<ProductDto> getProductDtoList(Map<Long, Long> cartInfo) {
        return productRepository.findAllById(cartInfo.keySet()).stream()
                .map(this::getProductDto).toList();
    }

    @Override
    @Transactional
    public void setAmount(ProductEntity productEntity, Long amount) {
        stockService.updateStock(productEntity, amount);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long productId, Long productStatusCode) {
        getProductEntity(productId).setProductStatus(ProductStatus.of(productStatusCode));
    }

    @Override
    public ProductDto getProductDto(ProductEntity productEntity) {
        List<ProductImageDto> productImages = productImageService.getProductImages(productEntity);
        ProductDto productDto = productEntity.toDto();

        productDto.setAmount(stockService.getStock(productEntity));
        productDto.setProductImages(productImages);

        return productDto;
    }

    // 후에 페이징 처리 할 것
    // 다 때려박으면 이미지 용량 어쩔 건데...
    @Override
    public List<ProductDto> getAllProducts(int page, int itemPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);

        List<ProductEntity> productEntities = productRepository.findAll(pageable).getContent();

        Map<Long, ProductDto> productDtoInfo = productEntities.stream().map(ProductEntity::toDto)
                .collect(
                        Collectors.toMap(ProductDto::getProductId, Function.identity()));

        List<ProductImageDto> productImageDtoList = productImageService.getProductByPage(productEntities);

        for (ProductImageDto productImageDto : productImageDtoList) {
            productDtoInfo.get(productImageDto.getProductId()).getProductImages().add(productImageDto);
        }

        return new ArrayList<>(productDtoInfo.values());
    }

    @Override
    public List<ProductDto> getAllProductsBySeller(Long sellerId) {
        // Todo
        return null;
    }

    @Override
    public List<ProductDto> getAllByCategory(CategoryDto categoryDto) {
        // Todo
        return null;
    }
}
