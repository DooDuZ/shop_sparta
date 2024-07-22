package com.sparta.shop_sparta.service.product;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationResponseDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.repository.ProductRepository;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {

    protected final ProductImageService productImageService;
    protected final ProductRepository productRepository;
    protected final StockService stockService;
    protected final ReservationService reservationService;

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
        return productRepository.findAllById(cartInfo.keySet()).stream()
                .map(this::getProductDto).toList();
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