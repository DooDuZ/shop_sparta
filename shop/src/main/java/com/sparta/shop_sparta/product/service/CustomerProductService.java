package com.sparta.shop_sparta.product.service;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.repository.ProductRepository;
import com.sparta.shop_sparta.product.repository.ProductRedisRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("customerProductService")
@Transactional(readOnly = true)
@Slf4j
public class CustomerProductService extends ProductService {

    @Autowired
    public CustomerProductService(
            ProductImageService productImageService,
            ProductRepository productRepository,
            StockService stockService,
            ReservationService reservationService,
            ProductRedisRepository productRedisRepository
    ) {
        super(productImageService, productRepository, stockService, reservationService, productRedisRepository);
    }

    // 모든 상품 찾기, 사용하지 않는 게 좋다.
    public List<ProductDto> getAllProducts(int page, int itemPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);

        List<ProductEntity> productEntities = productRepository.findAll(pageable).getContent();

        return getProductDtos(productEntities);
    }

    public List<ProductDto> getAllProductsByProductStatus(int page, int itemPerPage, Long productStatus) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);

        if (productStatus == 1 || productStatus == 5) {
            throw new ProductException(ProductMessage.NOT_FOUND_PRODUCT);
        }

        List<ProductEntity> productEntities = productRepository.findAllByProductStatusAndIsDeletedFalse(pageable,
                ProductStatus.of(productStatus)).getContent();

        return getProductDtos(productEntities);
    }

    public List<ProductDto> getAllProductsBySeller(int page, int itemPerPage, Long sellerId) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);
        List<ProductEntity> productEntities = productRepository.findAllBySellerEntity_memberIdAndProductStatusAndIsDeletedFalse(
                pageable,
                sellerId,
                ProductStatus.ON_SALE
        ).getContent();
        return getProductDtos(productEntities);
    }

    public List<ProductDto> getAllByCategory(int page, int itemPerPage, Long categoryId) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);
        List<ProductEntity> productEntities = productRepository.findAllByCategoryEntity_CategoryIdAndProductStatusAndIsDeletedFalse(
                pageable,
                categoryId,
                ProductStatus.ON_SALE
        ).getContent();

        return getProductDtos(productEntities);
    }
}
