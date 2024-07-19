package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductImageType;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.ProductImageRepository;
import com.sparta.shop_sparta.util.Image.ImageUtil;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

//@Service("localStorageImageService")
public class LocalStorageImageService extends ProductImageHandler{
    private final String fileSeparator = System.getProperty("file.separator");
    private final String filepath =
            System.getProperty("user.dir") + fileSeparator + "src" + fileSeparator + "main" + fileSeparator
                    + "resources" + fileSeparator + "images" + fileSeparator;


    public LocalStorageImageService(ProductImageRepository productImageRepository) {
        super(productImageRepository);
    }

    @Override
    protected List<ProductImageEntity> getImageEntities(ProductEntity productEntity, List<MultipartFile> images,
                                                      ProductImageType productImageType) {
        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        try {
            for (byte i = 1; i <= images.size(); i++) {
                MultipartFile productImage = images.get(i - 1);

                String uniqueFilename = UUID.randomUUID().toString() + "_" + productImage.getOriginalFilename();
                File imageFile = new File(filepath + uniqueFilename);

                productImage.transferTo(imageFile);

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

    @Override
    public List<ProductImageDto> getProductImages(ProductEntity productEntity) {
        return productImageRepository.findAllByProductEntity(productEntity)
                .stream().map(ProductImageEntity::toDto)
                .peek(
                        dto -> dto.setImageData(encodeBase64(dto))
                ).toList();
    }

    private String encodeBase64(ProductImageDto productImageDto) {
        try {
            return ImageUtil.readAndEncodeImage(filepath + productImageDto.getImagePath());
        } catch (IOException e) {
            throw new ProductException(ProductMessage.FAIL_IO_IMAGE);
        }

        //return productImageDto.getImagePath();
    }

    @Override
    @Transactional
    public void updateProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails,
                                    List<MultipartFile> productDetailImages) {
        validateImageSize(productThumbnails, productDetailImages);
        deleteProductImages(productEntity);
        createProductImages(productEntity, productThumbnails, productDetailImages);
    }

    @Override
    @Transactional
    public void deleteProductImages(ProductEntity productEntity) {
        productImageRepository.deleteAllByProductEntity(productEntity);
    }

    @Override
    public List<ProductImageDto> getProductByPage(List<ProductEntity> productEntities) {
        return productImageRepository.findAllByProductEntityIn(productEntities).stream().map(ProductImageEntity::toDto)
                .peek(
                        dto -> dto.setImageData(encodeBase64(dto))
                ).toList();
    }
}
