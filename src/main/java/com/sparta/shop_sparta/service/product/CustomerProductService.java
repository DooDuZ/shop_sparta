package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("customerProductService")
@Transactional(readOnly = true)
public class CustomerProductService extends ProductService {

    @Autowired
    public CustomerProductService(ProductImageService productImageService, ProductRepository productRepository,
                                  StockService stockService) {
        super(productImageService, productRepository, stockService);
    }

    public ProductDto getProduct(Long productId) {
        ProductEntity productEntity = getProductEntity(productId);

        // 공개되지 않은 상품이면 throw
        if (productEntity.getProductStatus() == ProductStatus.NOT_PUBLISHED){
            throw new ProductException(ProductMessage.NOT_FOUND_PRODUCT);
        }

        return getProductDto(productEntity);
    }

    public ProductDto getProductDto(ProductEntity productEntity) {
        List<ProductImageDto> productImages = productImageService.getProductImages(productEntity);
        ProductDto productDto = productEntity.toDto();

        productDto.setAmount(stockService.getStock(productEntity));
        productDto.setProductImages(productImages);

        return productDto;
    }

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

    public List<ProductDto> getAllProductsBySeller(Long sellerId) {
        // Todo
        return null;
    }

    public List<ProductDto> getAllByCategory(CategoryDto categoryDto) {
        // Todo
        return null;
    }
}
