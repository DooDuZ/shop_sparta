package com.sparta.shop_sparta.product.service;

import com.sparta.common.constant.product.ProductImageType;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductImageEntity;
import com.sparta.shop_sparta.product.repository.ProductImageRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public abstract class ProductImageHandler implements ProductImageService {
    protected final int maxThumbnails = 5;
    protected final int maxDetail = 5;

    protected final ProductImageRepository productImageRepository;

    protected abstract List<ProductImageEntity> getImageEntities(ProductEntity productEntity, List<MultipartFile> images, ProductImageType productImageType);

    protected void validateImageSize(List<MultipartFile> thumbnails, List<MultipartFile> detailImages) {
        if (thumbnails.size() == 0) {
            throw new ProductException(ProductMessage.NOT_FOUND_THUMBNAIL);
        } else if (thumbnails.size() > maxThumbnails || detailImages.size() > maxDetail) {
            throw new ProductException(ProductMessage.NOT_FOUND_THUMBNAIL);
        }
    }

    @Override
    @Transactional
    public void createProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails,
                                    List<MultipartFile> productDetailImages) {
        validateImageSize(productThumbnails, productDetailImages);

        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        productImageEntityList.addAll(getImageEntities(productEntity, productThumbnails, ProductImageType.HEADER));
        productImageEntityList.addAll(getImageEntities(productEntity, productDetailImages, ProductImageType.BODY));

        productImageRepository.saveAll(productImageEntityList);
    }
}
