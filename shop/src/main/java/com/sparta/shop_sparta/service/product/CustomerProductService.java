package com.sparta.shop_sparta.service.product;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.repository.ProductRepository;
import com.sparta.shop_sparta.repository.memoryRepository.ProductRedisRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("customerProductService")
@Transactional(readOnly = true)
public class CustomerProductService extends ProductService {

    private final ProductRedisRepository productRedisRepository;

    @Autowired
    public CustomerProductService(
            ProductImageService productImageService,
            ProductRepository productRepository,
            StockService stockService,
            ReservationService reservationService, ProductRedisRepository productRedisRepository
    ) {
        super(productImageService, productRepository, stockService, reservationService);
        this.productRedisRepository = productRedisRepository;
    }

    public ProductDto getProduct(Long productId) {
        String key = String.valueOf(productId);

        if (isCached(key)){
            return (ProductDto) productRedisRepository.find(key);
        }

        ProductEntity productEntity = getProductEntity(productId);

        // 공개되지 않았거나 숨김 처리된 상품이면 throw
        ProductStatus productStatus = productEntity.getProductStatus();
        if (productStatus == ProductStatus.NOT_PUBLISHED || productStatus == ProductStatus.SUSPENDED_SALE){
            throw new ProductException(ProductMessage.NOT_FOUND_PRODUCT);
        }

        ProductDto productDto = getProductDto(productEntity);
        productDto.setAmount(0L);
        productRedisRepository.saveWithDuration(key, productDto);

        return productDto;
    }

    private boolean isCached(String key){
        return productRedisRepository.hasKey(key);
    }

    // 모든 상품 찾기, 사용하지 않는 게 좋다.
    // Todo 예약 시간 리턴 방법 고민
    public List<ProductDto> getAllProducts(int page, int itemPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);

        List<ProductEntity> productEntities = productRepository.findAll(pageable).getContent();

        return getProductDtos(productEntities);
    }

    public List<ProductDto> getAllProductsByProductStatus(int page, int itemPerPage, Long productStatus) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);

        List<ProductEntity> productEntities = productRepository.findAllByProductStatus(pageable, ProductStatus.of(productStatus)).getContent();

        return getProductDtos(productEntities);
    }

    public List<ProductDto> getAllProductsBySeller(int page, int itemPerPage, Long sellerId) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);
        List<ProductEntity> productEntities = productRepository.findAllBySellerEntity_memberIdAndProductStatus(
                pageable,
                sellerId,
                ProductStatus.ON_SALE
        ).getContent();
        return getProductDtos(productEntities);
    }

    public List<ProductDto> getAllByCategory(int page, int itemPerPage, Long categoryId) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);
        List<ProductEntity> productEntities = productRepository.findAllByCategoryEntity_CategoryIdAndProductStatus(
                pageable,
                categoryId,
                ProductStatus.ON_SALE
        ).getContent();

        return getProductDtos(productEntities);
    }
}
