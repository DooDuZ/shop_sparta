package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    void createProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages);
    List<ProductImageDto> getProductImages(ProductEntity productEntity);
    void deleteProductImages(ProductEntity productEntity);
    void updateProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages);
    List<ProductImageDto> getProductByPage(List<ProductEntity> productEntities);
}
