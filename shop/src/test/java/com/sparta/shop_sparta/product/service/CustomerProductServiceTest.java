package com.sparta.shop_sparta.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.repository.ProductRedisRepository;
import com.sparta.shop_sparta.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public class CustomerProductServiceTest {

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @Autowired
    private CustomerProductService customerProductService;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductRedisRepository productRedisRepository;

    private ProductEntity cachedProductEntity;
    private ProductEntity notCachedProductEntity;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        productRedisRepository.flushAll();

        cachedProductEntity = ProductEntity.builder()
                .productId(1L)
                .productStatus(ProductStatus.ON_SALE)
                .productName("캐시 상품")
                .productDetail("빠르다")
                .categoryEntity(CategoryEntity.builder().categoryId(1L).categoryName("콤퓨타").build())
                .imageVersion(1L)
                .sellerEntity(MemberEntity.builder().memberId(1L).build())
                .build();

        notCachedProductEntity = ProductEntity.builder()
                .productId(2L)
                .productStatus(ProductStatus.ON_SALE)
                .productName("DB 상품")
                .productDetail("느리다")
                .categoryEntity(CategoryEntity.builder().categoryId(1L).categoryName("콤퓨타").build())
                .imageVersion(1L)
                .sellerEntity(MemberEntity.builder().memberId(1L).build())
                .build();
    }

    @Nested
    @DisplayName("상품 상세 조회 테스트")
    class GetProductTest{

        @Test
        @DisplayName("캐시된 상품을 조회합니다.")
        void getCachedProductSuccessTest(){
            // given
            productRedisRepository.save("1", cachedProductEntity.toDto());

            // when
            ProductDto productDto = customerProductService.getProduct(1L);

            // Then
            assertThat(productDto).isNotNull();
            assertThat(productDto.getProductId()).isEqualTo(1L);
            assertThat(productDto.getProductStatus()).isEqualTo(ProductStatus.ON_SALE);
        }


        @Test
        @DisplayName("캐사되지 않은 상품을 조회합니다.")
        void getUncachedProductSuccessTest() {
            // given
            Long productId = 1L;

            // 따로 캐시하지 않은 상태
            when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(cachedProductEntity));

            // when
            ProductDto result = customerProductService.getProduct(productId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getProductId()).isEqualTo(productId);
            assertThat(productRedisRepository.hasKey(String.valueOf(productId))).isTrue();

            verify(productRepository, times(1)).findById(productId);
        }

        @ParameterizedTest
        @EnumSource(value = ProductStatus.class, names = {"SUSPENDED_SALE", "NOT_PUBLISHED"})
        @DisplayName("공개 중이 아닌 상품을 조회하면 Exception이 발생합니다.")
        void getProductFailTest_NotPublished(ProductStatus productStatus) {
            // given
            Long productId = 2L;
            notCachedProductEntity.setProductStatus(productStatus);

            when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(notCachedProductEntity));

            // when then
            assertThatThrownBy(
                    () -> customerProductService.getProduct(productId)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.NOT_FOUND_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 상품을 조회하면 Exception이 발생합니다.")
        void getProductFailTest_InvalidProductId() {
            // given
            Long productId = 3L;

            // when then
            assertThatThrownBy(
                    () -> customerProductService.getProduct(productId)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.NOT_FOUND_PRODUCT.getMessage());
        }
    }

    @Nested
    @DisplayName("전체 상품 조회 테스트")
    class GetAllProductsByProductStatusTest {

        @ParameterizedTest
        @ValueSource(longs = {2, 3, 4})
        @DisplayName("공개된 상품 목록 조회에 성공합니다.")
        void getAllProductsByProductStatusSuccessTest(Long productStatus) {
            // given
            int page = 1;
            int itemPerPage = 10;
            List<ProductEntity> productEntities = Arrays.asList(cachedProductEntity, notCachedProductEntity);
            Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities);

            when(productRepository.findAllByProductStatusAndIsDeletedFalse(any(), eq(ProductStatus.of(productStatus))))
                    .thenReturn(productEntityPage);

            // when
            List<ProductDto> result = customerProductService.getAllProductsByProductStatus(page, itemPerPage, productStatus);

            // then
            assertThat(result).hasSize(2);
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 5L})
        @DisplayName("공개되지 않은 상품을 조회하면 Exception이 발생합니다.")
        void getAllProductsByProductStatusFailTest_InvalidStatus(Long productStatus) {
            // given
            int page = 1;
            int itemPerPage = 10;

            // when & then
            assertThatThrownBy(() ->
                    customerProductService.getAllProductsByProductStatus(page, itemPerPage, productStatus)
            ).isInstanceOf(ProductException.class)
                    .hasMessage(ProductMessage.NOT_FOUND_PRODUCT.getMessage());
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 0})
        @DisplayName("0이하의 페이지가 입력되면 Exception이 발생합니다.")
        void getAllProductsByProductStatusFailTest_InvalidPage(int page) {
            // given
            int itemPerPage = 10;

            // when & then
            assertThatThrownBy(() ->
                    customerProductService.getAllProductsByProductStatus(page, itemPerPage, 3L)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 0})
        @DisplayName("0이하의 아이템 수가 입력되면 Exception이 발생합니다.")
        void getAllProductsByProductStatusFailTest_InvalidItemSize(int itemPerPage) {
            // given
            int page = 1;

            // when & then
            assertThatThrownBy(() ->
                    customerProductService.getAllProductsByProductStatus(page, itemPerPage, 3L)
            ).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getAllProductsBySeller 메서드 테스트")
    class GetAllProductsBySellerTests {

        @Test
        @DisplayName("판매자 ID로 상품 목록을 조회하면 성공합니다.")
        void getAllProductsBySellerSuccessTest() {
            // given
            int page = 1;
            int itemPerPage = 10;
            Long sellerId = 1L;
            List<ProductEntity> productEntities = Arrays.asList(cachedProductEntity, notCachedProductEntity);
            Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities);

            when(productRepository.findAllBySellerEntity_memberIdAndProductStatusAndIsDeletedFalse(
                    any(), eq(sellerId), eq(ProductStatus.ON_SALE)))
                    .thenReturn(productEntityPage);

            // when
            List<ProductDto> result = customerProductService.getAllProductsBySeller(page, itemPerPage, sellerId);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("판매자가 아닌 ID로 상품 목록을 조회하면 아무 상품도 반환되지 않습니다.")
        void getAllProductsBySellerFailTest_InvalidSellerId() {
            // given
            int page = 1;
            int itemPerPage = 10;
            Long sellerId = 3L;
            List<ProductEntity> productEntities = new ArrayList<>();
            Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities);

            when(productRepository.findAllBySellerEntity_memberIdAndProductStatusAndIsDeletedFalse(
                    any(), eq(sellerId), eq(ProductStatus.ON_SALE)))
                    .thenReturn(productEntityPage);

            // when
            List<ProductDto> result = customerProductService.getAllProductsBySeller(page, itemPerPage, sellerId);

            // then
            assertThat(result).hasSize(0);
        }
    }

    @Nested
    @DisplayName("getAllByCategory 메서드 테스트")
    class GetAllByCategoryTests {

        @Test
        @DisplayName("카테고리 ID로 상품 목록을 조회하면 성공합니다.")
        void getAllByCategorySuccessTest() {
            // given
            int page = 1;
            int itemPerPage = 10;
            Long categoryId = 1L;
            List<ProductEntity> productEntities = Arrays.asList(cachedProductEntity);
            Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities);

            when(productRepository.findAllByCategoryEntity_CategoryIdAndProductStatusAndIsDeletedFalse(
                    any(), eq(categoryId), eq(ProductStatus.ON_SALE)))
                    .thenReturn(productEntityPage);

            // when
            List<ProductDto> result = customerProductService.getAllByCategory(page, itemPerPage, categoryId);

            // then
            assertThat(result).hasSize(1);
        }
    }
}
