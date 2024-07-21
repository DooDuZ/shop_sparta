package com.sparta.batch.image.processor;

import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.batch.domain.entity.product.ProductImageEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageProcessor implements ItemProcessor<ProductImageEntity, ProductImageEntity> {

    private final AmazonS3Client amazonS3Client;

    @Value("${S3_BUCKET}")
    private String bucket;

    @Override
    public ProductImageEntity process(ProductImageEntity productImageEntity){
        String url = productImageEntity.getImagePath();
        String key = productImageEntity.getImagePath().split("com/")[1];

        try {
            /*URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();

            String key =  path.startsWith("/") ? path.substring(1) : path;*/

            amazonS3Client.deleteObject(bucket, key);
            productImageEntity.setLastModifyDate(LocalDateTime.now());
            productImageEntity.setDelete(true);
            log.info("Deleted S3 object: {}", url);
        } catch (Exception e) {
            log.error("Failed to delete S3 object: {}", url, e);
        }

        return productImageEntity;
    }
}
