package com.sparta.shop_sparta.service.product;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.common.constant.product.ProductImageType;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import com.sparta.shop_sparta.repository.ProductImageRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class S3ImageService extends ProductImageHandler {

    private final AmazonS3Client amazonS3Client;

    @Value("${S3_BUCKET}")
    private String bucket;

    public S3ImageService(ProductImageRepository productImageRepository, AmazonS3Client amazonS3Client) {
        super(productImageRepository);
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    protected List<ProductImageEntity> getImageEntities(
            ProductEntity productEntity,
            List<MultipartFile> images,
            ProductImageType productImageType
    ) {
        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        try {
            for (byte i = 1; i <= images.size(); i++) {
                String fileName = uploadToS3(images.get(i - 1));

                productImageEntityList.add(
                        ProductImageEntity.builder()
                                .productEntity(productEntity)
                                .imageVersion(productEntity.getImageVersion())
                                .productImageType(productImageType)
                                .imageOrdering(i)
                                .imagePath(fileName).build()
                );
            }
        } catch (AmazonS3Exception e) {
            throw new ProductException(ProductMessage.FAIL_S3_UPLOAD, e);
        } catch (IOException e) {
            throw new ProductException(ProductMessage.FAIL_IO_IMAGE, e);
        }

        return productImageEntityList;
    }

    private String uploadToS3(MultipartFile productImage) throws IOException, AmazonS3Exception {
        String key = UUID.randomUUID().toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(productImage.getContentType());
        objectMetadata.setContentLength(productImage.getSize());

        amazonS3Client.putObject(bucket, key, productImage.getInputStream(), objectMetadata);

        String url = amazonS3Client.getUrl(bucket, key).toString();

        return url;
    }

    @Override
    public List<ProductImageDto> getProductImages(ProductEntity productEntity) {
        return productImageRepository.findAllByProductAndImageVersion(productEntity.getProductId())
                .stream().map(ProductImageEntity::toDto)
                .toList();
    }

    @Override
    public List<ProductImageDto> getProductByPage(List<ProductEntity> productEntities) {
        List<Long> productIds = productEntities.stream().map(ProductEntity::getProductId).collect(Collectors.toList());

        return productImageRepository.findAllByProductsAndImageVersion(productIds).stream().map(ProductImageEntity::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails,
                                    List<MultipartFile> productDetailImages) {
        productEntity.updateVersion();
        createProductImages(productEntity, productThumbnails, productDetailImages);
    }

    @Override
    public void deleteProductImages(ProductEntity productEntity) {

    }
}
