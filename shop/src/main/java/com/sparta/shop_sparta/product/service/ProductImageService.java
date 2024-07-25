package com.sparta.shop_sparta.product.service;

import com.sparta.shop_sparta.product.domain.dto.ProductImageDto;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    void createProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages);
    public List<ProductImageDto> getProductImages(ProductEntity productEntity);
    public void updateProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages);
    public void deleteProductImages(ProductEntity productEntity);
    public List<ProductImageDto> getProductByPage(List<ProductEntity> productEntities);
}
