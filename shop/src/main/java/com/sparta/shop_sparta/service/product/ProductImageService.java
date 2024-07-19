package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    void createProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages);
    public List<ProductImageDto> getProductImages(ProductEntity productEntity);
    public void updateProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages);
    public void deleteProductImages(ProductEntity productEntity);
    public List<ProductImageDto> getProductByPage(List<ProductEntity> productEntities);
}
