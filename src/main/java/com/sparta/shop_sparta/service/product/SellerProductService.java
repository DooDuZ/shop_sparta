package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationRequestDto;
import com.sparta.shop_sparta.domain.dto.product.ReservationResponseDto;
import com.sparta.shop_sparta.domain.dto.product.StockRequestDto;
import com.sparta.shop_sparta.domain.dto.product.StockResponseDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.CategoryRepository;
import com.sparta.shop_sparta.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sellerProductService")
@Transactional(readOnly = true)
public class SellerProductService extends ProductService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public SellerProductService(ProductImageService productImageService, ProductRepository productRepository,
                                StockService stockService, CategoryRepository categoryRepository,
                                ReservationService reservationService) {
        super(productImageService, productRepository, stockService, reservationService);
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductDto createProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        ProductEntity productEntity = productRequestDto.toEntity();

        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_CATEGORY)
        );

        productEntity.init(categoryEntity, (MemberEntity) userDetails);

        ProductEntity product = productRepository.save(productEntity);

        stockService.addProduct(productEntity, productRequestDto.getAmount());

        productImageService.createProductImages(
                productEntity, productRequestDto.getProductThumbnails(),
                productRequestDto.getProductDetailImages()
        );

        if(productRequestDto.isReservation() && productRequestDto.getReservationTime() != null) {
            reservationService.createReservation(
                    productEntity,
                    productRequestDto.getReservationTime(),
                    ProductStatus.of(productRequestDto.getReservationStatus())
            );
        }

        return product.toDto();
    }

    public List<ProductDto> getSellerProducts(UserDetails userDetails, int page, int itemsPerPage, Long productStatus) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        
        List<ProductEntity> productEntities = productRepository.findAllBySellerEntityAndProductStatus(
                pageable,
                memberEntity,
                ProductStatus.of(productStatus)
        ).getContent();

        return getProductDtos(productEntities);
    }

    @Transactional
    public ProductDto updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        productEntity.update(productRequestDto);
        stockService.updateStock(productEntity, productRequestDto.getAmount());

        // [Todo] 이미지 update 적용
        // version 관리 방법 고민 후 적용

        return productEntity.toDto();
    }

    @Transactional
    public void deleteProduct(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productId);

        // 래퍼 클래스에 == 쓰면 참조값을 비교한다. + or - 연산 시 자동 언박싱됨
        if (memberEntity.getMemberId() - productEntity.getSellerEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        productEntity.setDelete(true);
    }

    @Transactional
    public StockResponseDto updateStock(UserDetails userDetails, StockRequestDto stockRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = getProductEntity(stockRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        StockEntity stockEntity = setAmount(productEntity, stockRequestDto.getAmount());

        return StockResponseDto.builder()
                .productId(stockEntity.getProductEntity().getProductId())
                .amount(stockEntity.getAmount())
                .build();
    }

    @Transactional
    public StockEntity setAmount(ProductEntity productEntity, Long amount) {
        return stockService.updateStock(productEntity, amount);
    }

    @Transactional
    public void updateProductStatus(Long productId, Long productStatusCode) {
        getProductEntity(productId).setProductStatus(ProductStatus.of(productStatusCode));
    }

    @Transactional
    public void updateProductStatus(Long productId, ProductStatus productStatus) {
        getProductEntity(productId).setProductStatus(productStatus);
    }

    @Transactional
    public ReservationResponseDto createReservation(UserDetails userDetails,
                                                    ReservationRequestDto reservationRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = getProductEntity(reservationRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        return reservationService.createReservation(productEntity, reservationRequestDto.getReservationTime(), ProductStatus.of(
                reservationRequestDto.getReserveStatus()));
    }

    @Transactional
    public ReservationResponseDto updateReservation(UserDetails userDetails,
                                                    ReservationRequestDto reservationRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = getProductEntity(reservationRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        return reservationService.updateReservation(reservationRequestDto);
    }

    @Transactional
    public void cancelReservation(UserDetails userDetails, Long reservationId) {
        reservationService.cancelReservation((MemberEntity) userDetails, reservationId);
    }
}
