package com.sparta.shop_sparta.product.service;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.dto.ProductRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ProductStatusRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationResponseDto;
import com.sparta.shop_sparta.product.domain.dto.StockRequestDto;
import com.sparta.shop_sparta.product.domain.dto.StockResponseDto;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.product.repository.ProductRepository;
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

    private final CategoryService categoryService;

    @Autowired
    public SellerProductService(
            ProductImageService productImageService,
            ProductRepository productRepository,
            StockService stockService,
            CategoryService categoryService,
            ReservationService reservationService
    ) {
        super(productImageService, productRepository, stockService, reservationService);
        this.categoryService = categoryService;
    }

    @Transactional
    public ProductDto createProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        ProductEntity productEntity = productRequestDto.toEntity();

        CategoryEntity categoryEntity = categoryService.getCategoryEntity(productRequestDto.getCategoryId());

        productEntity.init(categoryEntity, (MemberEntity) userDetails);

        productRepository.save(productEntity);

        stockService.addProduct(productEntity, productRequestDto.getAmount());

        productImageService.createProductImages(
                productEntity, productRequestDto.getProductThumbnails(),
                productRequestDto.getProductDetailImages()
        );

        if(!productRequestDto.getReservations().isEmpty()) {
            createReservations(productEntity, productRequestDto.getReservations());
        }

        return getProductDto(productEntity);
    }

    private void createReservations(ProductEntity productEntity, List<ReservationRequestDto> reservations){
        for(ReservationRequestDto reservationRequestDto : reservations) {
            reservationService.createReservation(
                    productEntity,
                    reservationRequestDto.getReservationTime(),
                    reservationRequestDto.getReservationStatus()
            );
        }
    }

    public List<ProductDto> getSellerProducts(UserDetails userDetails, int page, int itemsPerPage) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        
        List<ProductEntity> productEntities = productRepository.findAllBySellerEntityAndIsDeletedFalse(pageable, memberEntity).getContent();

        return getProductDtos(productEntities);
    }

    @Transactional
    public ProductDto updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productId);

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        productEntity.update(productRequestDto);
        stockService.updateStock(productEntity, productRequestDto.getAmount());
        // 이미지 교체 여부 확인 해야함
        productImageService.updateProductImages(productEntity, productRequestDto.getProductThumbnails(), productRequestDto.getProductDetailImages());

        if (!productRequestDto.getReservations().isEmpty()) {
            reservationService.updateReservations(productId, productRequestDto.getReservations());
        }

        return getProductDto(productEntity);
    }

    // controller를 통해 바꿀 일이 있다면 사용
    @Transactional
    public List<ReservationResponseDto> updateReservations(UserDetails userDetails, ProductEntity productEntity, List<ReservationRequestDto> reservations) {
        return reservationService.updateReservations(productEntity.getProductId(), reservations);
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
    public void updateProductStatus(UserDetails userDetails, ProductStatusRequestDto productStatusRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = getProductEntity(productStatusRequestDto.getProductId());

        if(memberEntity.getMemberId() - productEntity.getSellerEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        productEntity.setProductStatus(ProductStatus.of(productStatusRequestDto.getProductStatus()));
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

        return reservationService.createReservation(productEntity, reservationRequestDto.getReservationTime(), reservationRequestDto.getReservationStatus());
    }

    @Transactional
    public ReservationResponseDto updateReservation(
            UserDetails userDetails,
            ReservationRequestDto reservationRequestDto,
            Long reservationId
    ) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        ProductEntity productEntity = getProductEntity(reservationRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        reservationRequestDto.setReservationId(reservationId);

        return reservationService.updateReservation(reservationRequestDto);
    }

    @Transactional
    public void cancelReservation(UserDetails userDetails, Long reservationId) {
        reservationService.cancelReservation((MemberEntity) userDetails, reservationId);
    }
}
