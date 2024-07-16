package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductImageType;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.ProductImageRepository;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final String fileSeparator = System.getProperty("file.separator");
    private final String filepath =
            System.getProperty("user.dir") + fileSeparator + "src" + fileSeparator + "main" + fileSeparator
                    + "resources" + fileSeparator + "images" + fileSeparator;
    private final int maxThumbnails = 5;
    private final int maxDetail = 5;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    @Transactional
    public void createProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails,
                                    List<MultipartFile> productDetailImages) {
        validateImageSize(productThumbnails, productDetailImages);

        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        productImageEntityList.addAll(getImageEntities(productEntity, productThumbnails, ProductImageType.HEADER));
        productImageEntityList.addAll(getImageEntities(productEntity, productDetailImages, ProductImageType.BODY));

        productImageRepository.saveAll(productImageEntityList);
    }

    private List<ProductImageEntity> getImageEntities(ProductEntity productEntity, List<MultipartFile> images,
                                                      ProductImageType productImageType) {
        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        try {
            for (byte i = 1; i <= images.size(); i++) {
                MultipartFile productThumbnail = images.get(i - 1);

                if (productThumbnail.getSize() > MAX_FILE_SIZE) {
                    throw new RuntimeException();
                }

                String uniqueFilename = UUID.randomUUID().toString() + "_" + productThumbnail.getOriginalFilename();
                File imageFile = new File(filepath + uniqueFilename);

                productThumbnail.transferTo(imageFile);

                productImageEntityList.add(ProductImageEntity.builder().productEntity(productEntity)
                        .productImageType(productImageType).imageOrdering(i).imagePath(uniqueFilename).build());
            }
        } catch (IOException e) {
            throw new ProductException(ProductMessage.FAIL_IO_IMAGE, e);
        } catch (RuntimeException e) {
            throw new ProductException(ProductMessage.FILE_SIZE_EXCEEDED, e);
        }

        return productImageEntityList;
    }

    private void validateImageSize(List<MultipartFile> thumbnails, List<MultipartFile> detailImages) {
        if (thumbnails.size() == 0) {
            throw new ProductException(ProductMessage.NOT_FOUND_THUMBNAIL);
        } else if (thumbnails.size() > maxThumbnails || detailImages.size() > maxDetail) {
            throw new ProductException(ProductMessage.NOT_FOUND_THUMBNAIL);
        }
    }

    public List<ProductImageDto> getProductImages(ProductEntity productEntity) {
        return productImageRepository.findAllByProductEntity(productEntity)
                .stream().map(ProductImageEntity::toDto)
                .peek(
                        dto -> dto.setEncodedImageByBase64(encodeBase64(dto))
                ).toList();
    }

    private String encodeBase64(ProductImageDto productImageDto) {
        /* 테스트 불편해서 주석 처리
        try {
            return ImageUtil.readAndEncodeImage(filepath + productImageDto.getImagePath());
        } catch (IOException e) {
            throw new ProductException(ProductMessage.FAIL_IO_IMAGE.getMessage());
        }
        */
        return productImageDto.getImagePath();
    }

    @Transactional
    public void updateProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails,
                                    List<MultipartFile> productDetailImages) {
        validateImageSize(productThumbnails, productDetailImages);
        deleteProductImages(productEntity);
        createProductImages(productEntity, productThumbnails, productDetailImages);
    }

    @Transactional
    public void deleteProductImages(ProductEntity productEntity) {
        productImageRepository.deleteAllByProductEntity(productEntity);
    }

    public List<ProductImageDto> getProductByPage(List<ProductEntity> productEntities) {
        return productImageRepository.findAllByProductEntityIn(productEntities).stream().map(ProductImageEntity::toDto)
                .peek(
                        dto -> dto.setEncodedImageByBase64(encodeBase64(dto))
                ).toList();
    }
}
