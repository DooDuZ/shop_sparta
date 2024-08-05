package com.sparta.shop_sparta.product.service;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.dto.ProductImageDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationResponseDto;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.repository.ProductRedisRepository;
import com.sparta.shop_sparta.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    protected final ProductImageService productImageService;
    protected final ProductRepository productRepository;
    protected final StockService stockService;
    protected final ReservationService reservationService;
    protected final ProductRedisRepository productRedisRepository;

    public ProductEntity getProductEntity(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT)
        );
    }

    public ProductDto getProductDto(ProductEntity productEntity) {
        List<ProductImageDto> productImages = productImageService.getProductImages(productEntity);
        ProductDto productDto = productEntity.toDto();

        productDto.setAmount(stockService.getStock(productEntity));
        productDto.setProductImages(productImages);

        productDto.setReservationResponseDtoList(reservationService.getReservationsByProductEntity(productEntity));

        return productDto;
    }

    public List<ProductDto> getProductDtoList(Map<Long, Long> cartInfo) {
        List<ProductDto> productDtoList = new ArrayList<>();

        for (Long key : cartInfo.keySet()) {
            productDtoList.add(getProduct(key));
        }

        return productRepository.findAllById(cartInfo.keySet()).stream()
                .map(this::getProductDto).toList();
    }

    public ProductDto getProduct(Long productId) {
        String key = String.valueOf(productId);

        if (isCached(key)) {
            return (ProductDto) productRedisRepository.find(key);
        }

        ProductEntity productEntity = getProductEntity(productId);

        // 공개되지 않았거나 숨김 처리된 상품이면 throw
        ProductStatus productStatus = productEntity.getProductStatus();
        if (productStatus == ProductStatus.NOT_PUBLISHED || productStatus == ProductStatus.SUSPENDED_SALE) {
            throw new ProductException(ProductMessage.NOT_FOUND_PRODUCT);
        }

        ProductDto productDto = getProductDto(productEntity);
        productDto.setAmount(0L);
        productRedisRepository.cache(key, productDto);

        return productDto;
    }

    private boolean isCached(String key) {
        return productRedisRepository.hasKey(key);
    }

    public List<ProductDto> getProductDtos(List<ProductEntity> productEntities) {
        Map<Long, ProductDto> productDtoInfo = productEntities.stream().map(ProductEntity::toDto)
                .collect(
                        Collectors.toMap(ProductDto::getProductId, Function.identity())
                );

        setImages(productEntities, productDtoInfo);
        setReservations(productEntities, productDtoInfo);

        return new ArrayList<>(productDtoInfo.values());
    }

    private void setImages(List<ProductEntity> productEntities, Map<Long, ProductDto> productDtoInfo) {
        List<ProductImageDto> productImageDtoList = productImageService.getProductByPage(productEntities);

        for (ProductImageDto productImageDto : productImageDtoList) {
            productDtoInfo.get(productImageDto.getProductId()).getProductImages().add(productImageDto);
        }
    }

    private void setReservations(List<ProductEntity> productEntities, Map<Long, ProductDto> productDtoInfo) {
        List<ReservationResponseDto> reservationResponseDtoList = reservationService.getAllReservationsByProductEntities(productEntities);

        for (ReservationResponseDto reservationResponseDto : reservationResponseDtoList) {
            productDtoInfo.get(reservationResponseDto.getProductId()).getReservationResponseDtoList().add(reservationResponseDto);
        }
    }
}