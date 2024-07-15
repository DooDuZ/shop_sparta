package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationResponseDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProductService {
    ProductDto createProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    ProductDto updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto);
    void deleteProduct(UserDetails userDetails, Long productId);
    ProductDto getProduct(Long productId);
    ProductEntity getProductEntity(Long productId);
    List<ProductDto> getAllProducts(int page, int itemsPerPage);
    List<ProductDto> getAllProductsBySeller(Long sellerId);
    List<ProductDto> getAllByCategory(CategoryDto categoryDto);
    ProductDto getProductDto(ProductEntity productEntity);
    List<ProductDto> getProductDtoList(Map<Long, Long> cartInfo);
    void setAmount(ProductEntity productEntity, Long amount);
    void updateProductStatus(Long productId, Long productStatusCode);
    void updateProductStatus(Long productId, ProductStatus productStatus);

    ReservationResponseDto createReservation(UserDetails userDetails, ReservationRequestDto reservationRequestDto);
    ReservationResponseDto updateReservation(UserDetails userDetails, ReservationRequestDto reservationRequestDto);
    void cancelReservation(UserDetails userDetails, Long reservationId);
}
