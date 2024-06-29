package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.product.ProductImageType;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductImageEntity;
import com.sparta.shop_sparta.exception.product.ProductException;
import com.sparta.shop_sparta.repository.ProductImageRepository;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final String fileSeparator = System.getProperty("file.separator");
    private final String filepath = System.getProperty("user.dir") + fileSeparator + "src" + fileSeparator + "main" + fileSeparator
            + "resources" + fileSeparator + "images" + fileSeparator;
    private final int maxThumbnails = 5;
    private final int maxDetail = 5;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    @Override
    @Transactional
    public void addProductImages(ProductEntity productEntity, List<MultipartFile> productThumbnails, List<MultipartFile> productDetailImages) {
        validateImageSize(productThumbnails, productDetailImages);

        System.out.println("ProductImageServiceImpl.addProductImages");
        System.out.println("filesize : " + productThumbnails.size());
        System.out.println("filesize2 : " + productDetailImages.size());

        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        productImageEntityList.addAll(getImageEntities(productEntity, productThumbnails, ProductImageType.HEADER));
        productImageEntityList.addAll(getImageEntities(productEntity, productDetailImages, ProductImageType.BODY));

        try {
            // 만약 여러장 이미지 저장이 실패하면 Exception 발생시켜서 호출 메서드에서 알 수 있게 해야함
            // FK사용을 위해 저장된 ProductEntity를 delete 해야줘야하기 떄문!!
            // ProductEntity엔 softDelete 적용돼있는데... 괜찮겠지 이정도는? -> 너무 많은 garbage값이 생긴다면 EntityListener를 풀고 물리적 삭제 기능을 살려 두는 것도 방법
            productImageRepository.saveAll(productImageEntityList);
        }catch (Exception e){
            // DB 커넥션 풀 부족 등의 문제로 실패할 가능성이 있음
            // 그러나 시스템 외적인 문제도 고려해야함
            e.printStackTrace();
        }
    }

    private List<ProductImageEntity> getImageEntities(ProductEntity productEntity, List<MultipartFile> images, ProductImageType productImageType) {
        List<ProductImageEntity> productImageEntityList = new ArrayList<>();

        System.out.println("images.size() = " + images.size());

        try {
            for (byte i = 1; i <= images.size(); i++){
                MultipartFile productThumbnail = images.get(i - 1);

                if (productThumbnail.getSize() > MAX_FILE_SIZE){
                    throw new RuntimeException();
                }

                String uniqueFilename = UUID.randomUUID().toString() + "_" + productThumbnail.getOriginalFilename();
                File imageFile = new File(filepath + uniqueFilename);

                productThumbnail.transferTo(imageFile);

                productImageEntityList.add(ProductImageEntity.builder().productEntity(productEntity)
                        .productImageType(productImageType).imageOrdering(i).imagePath(uniqueFilename).build());
            }
        }catch (IOException e){
            e.printStackTrace();
            // 파일만 저장되고 Entity로 저장이 안되는 경우에도 참조값 비교를 통해 주기적으로 지워지도록 구현 예정
            // 저장한 파일 신경쓰지 말고 exception 발생!
            throw new ProductException(ProductMessage.FAIL_STORE_IMAGE.getMessage(), e);
        }catch (RuntimeException e){
            throw new ProductException(ProductMessage.FILE_SIZE_EXCEEDED.getMessage(), e);
        }

        return productImageEntityList;
    }

    private void validateImageSize(List<MultipartFile> thumbnails, List<MultipartFile> detailImages){
        if (thumbnails.size() == 0) {
            throw new ProductException(ProductMessage.NOT_FOUND_THUMBNAIL.getMessage());
        }else if(thumbnails.size() > maxThumbnails || detailImages.size() > maxDetail){
            throw new ProductException(ProductMessage.NOT_FOUND_THUMBNAIL.getMessage());
        }
    }

    @Override
    public List<ProductImageDto> getProductImages(ProductEntity productEntity) {
        return List.of();
    }

    @Override
    @Transactional
    public void deleteProductImages(ProductEntity productEntity) {

    }
}
