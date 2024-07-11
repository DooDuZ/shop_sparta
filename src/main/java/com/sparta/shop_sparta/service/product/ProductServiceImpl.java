package com.sparta.shop_sparta.service.product;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.product.CategoryDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.ProductException;
import com.sparta.shop_sparta.repository.CategoryRepository;
import com.sparta.shop_sparta.repository.ProductRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductImageService productImageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockService stockService;
    private final ReservationService reservationService;
    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks;

    @Override
    @Transactional
    public ProductDto createProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        ProductEntity productEntity = productRequestDto.toEntity();

        CategoryEntity categoryEntity = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(
                () -> new ProductException(ProductMessage.INVALID_CATEGORY.getMessage())
        );

        productEntity.init(categoryEntity, (MemberEntity) userDetails);

        ProductEntity product = productRepository.save(productEntity);

        stockService.addProduct(productEntity, productRequestDto.getAmount());
        //stockService.updateStock(productEntity, productRequestDto.getAmount());

        productImageService.createProductImages(productEntity, productRequestDto.getProductThumbnails(),
                productRequestDto.getProductDetailImages());

        if(productRequestDto.isReservation() && productRequestDto.getReservationTime() != null) {
            createOpenSchedule(productEntity.getProductId(), productRequestDto.getReservationTime());
        }

        System.out.println("상품등록 끝");
        return product.toDto();
    }

    @Transactional
    protected void createOpenSchedule(Long productId, LocalDateTime scheduledTime){
        Instant instant = scheduledTime.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule( ()-> {
            ProductEntity productEntity = getProductEntity(productId);
            productEntity.setProductStatus(ProductStatus.ON_SALE);
            productRepository.save(productEntity);
            reservationService.reservationCompleted(productEntity);
        }, instant);

        reservationService.createReservation(getProductEntity(productId), scheduledTime);
        scheduledTasks.put(productId, scheduledFuture);
    }

    protected void updateOpenSchedule(Long productId, LocalDateTime scheduledTime){
        cancelOpenSchedule(productId);
        createOpenSchedule(productId, scheduledTime);
    }

    protected void cancelOpenSchedule(Long productId){
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(productId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false); // 예약된 작업 취소
            scheduledTasks.remove(productId); // 맵에서 작업 제거
        }

        reservationService.reservationCompleted(getProductEntity(productId));
    }


    @Override
    @Transactional
    public ProductDto updateProduct(UserDetails userDetails, ProductRequestDto productRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productRequestDto.getProductId());

        if (productEntity.getSellerEntity().getMemberId() - memberEntity.getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        productEntity.update(productRequestDto);
        stockService.updateStock(productEntity, productRequestDto.getAmount());
        //StockEntity stockEntity = stockService.getStockEntity(productEntity);
        //stockEntity.setAmount(productRequestDto.getAmount());

        // [Todo] 이미지 update 적용
        // version 관리 방법 고민 후 적용

        return productEntity.toDto();
    }

    @Override
    public void deleteProduct(UserDetails userDetails, Long productId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        ProductEntity productEntity = getProductEntity(productId);

        // 래퍼 클래스에 == 쓰면 참조값을 비교한다. + or - 연산 시 자동 언박싱됨
        if (memberEntity.getMemberId() - productEntity.getSellerEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        productEntity.setDelete(true);
    }

    @Override
    public ProductDto getProduct(Long productId) {
        ProductEntity productEntity = getProductEntity(productId);
        return getProductDto(productEntity);
    }

    // 주문에서 사용할 수 있도록 분리
    @Override
    public ProductEntity getProductEntity(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductMessage.NOT_FOUND_PRODUCT.getMessage())
        );
    }

    public List<ProductDto> getProductDtoList(Map<Long, Long> cartInfo) {
        return productRepository.findAllById(cartInfo.keySet()).stream()
                .map(this::getProductDto).toList();
    }

    @Override
    @Transactional
    public void setAmount(ProductEntity productEntity, Long amount) {
        stockService.updateStock(productEntity, amount);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long productId, Long productStatusCode) {
        getProductEntity(productId).setProductStatus(ProductStatus.of(productStatusCode));
    }

    @Override
    @Transactional
    public void updateProductStatus(Long productId, ProductStatus productStatus) {
        getProductEntity(productId).setProductStatus(productStatus);
    }

    @Override
    public ProductDto getProductDto(ProductEntity productEntity) {
        List<ProductImageDto> productImages = productImageService.getProductImages(productEntity);
        ProductDto productDto = productEntity.toDto();

        productDto.setAmount(stockService.getStock(productEntity));
        productDto.setProductImages(productImages);

        return productDto;
    }

    // 후에 페이징 처리 할 것
    // 다 때려박으면 이미지 용량 어쩔 건데...
    @Override
    public List<ProductDto> getAllProducts(int page, int itemPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemPerPage);

        List<ProductEntity> productEntities = productRepository.findAll(pageable).getContent();

        Map<Long, ProductDto> productDtoInfo = productEntities.stream().map(ProductEntity::toDto)
                .collect(
                        Collectors.toMap(ProductDto::getProductId, Function.identity()));

        List<ProductImageDto> productImageDtoList = productImageService.getProductByPage(productEntities);

        for (ProductImageDto productImageDto : productImageDtoList) {
            productDtoInfo.get(productImageDto.getProductId()).getProductImages().add(productImageDto);
        }

        return new ArrayList<>(productDtoInfo.values());
    }

    @Override
    public List<ProductDto> getAllProductsBySeller(Long sellerId) {
        // Todo
        return null;
    }

    @Override
    public List<ProductDto> getAllByCategory(CategoryDto categoryDto) {
        // Todo
        return null;
    }
}
