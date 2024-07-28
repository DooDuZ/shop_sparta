package com.sparta.shop_sparta.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.member.MemberRole;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.member.repository.MemberRepository;
import com.sparta.shop_sparta.product.domain.dto.CategoryDto;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.dto.ProductRequestDto;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.product.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class SellerProductServiceTest {
    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private SellerProductService sellerProductService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private CategoryService categoryService;

    // mock으로 안쓰면 s3에 실제로 저장된다
    // 돈 많으면 풀어라...
    @MockBean
    private ProductImageService productImageService;

    private static MemberEntity sellerEntity;
    private static StockEntity stockEntity;

    @BeforeAll
    static void init(@Autowired MemberRepository memberRepository,
                     @Autowired CategoryService categoryService,
                     @Autowired ProductRepository productRepository) {
        // sellerEntity
        sellerEntity = MemberEntity.builder()
                .memberName("판매자")
                .role(MemberRole.SELLER)
                .email("sin9158@naver.com")
                .phoneNumber("존나멋진전화번호")
                .loginId("seller1")
                .password("1231234")
                .build();
        memberRepository.save(sellerEntity);

        // category
        CategoryDto categoryDto = categoryService.addCategory(CategoryDto.builder().categoryName("ㅇㅇㅇ").build());

        // stock
        stockEntity = StockEntity.builder()
                .amount(100L)
                .stockId(1L)
                .build();

        long statusIndex = 0;

        for (int i = 0; i < 100; i++) {
            if(i%20==0){
                statusIndex++;
            }

            ProductEntity productEntity = ProductEntity.builder()
                    .sellerEntity(sellerEntity)
                    .productDetail("없어요")
                    .productName("사세요")
                    .imageVersion(1L)
                    .price(200000L)
                    .categoryEntity(categoryDto.toEntity())
                    .productStatus(ProductStatus.of(statusIndex))
                    .build();

            productRepository.save(productEntity);
        }
    }

    @Nested
    @DisplayName("[통합 테스트] 판매자 등록 상품 조회 테스트")
    class getProductTest {
        @Test
        @DisplayName("판매자 본인 상품 전체 조회 성공 테스트")
        void getProductSuccessTest(){
            // when
            List<ProductDto> products = sellerProductService.getSellerProducts(sellerEntity, 1, 100);

            // then
            assertThat(products.size()).isEqualTo(100);
        }

        @Test
        @DisplayName("유효하지 않은 페이지가 입력되면 Exception이 발생합니다.")
        void getProductFailTest_invalidPage(){
            // when then
            assertThatThrownBy(
                    () -> sellerProductService.getSellerProducts(sellerEntity, 0, 100)
            ).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상품 등록 테스트")
    class createProductTest {

        private ProductRequestDto requestDto;

        @BeforeEach
        void init(){
            requestDto = new ProductRequestDto();
            requestDto.setProductName("Test Product");
            requestDto.setProductDetail("Test Detail");
            requestDto.setCategoryId(1L);
            requestDto.setAmount(10L);
            requestDto.setPrice(10000L);
            requestDto.setReservation(true);
            requestDto.setReservationStatus(3L);
            requestDto.setReservationTime(LocalDateTime.now());
        }

        @Test
        @DisplayName("상품 생성 성공 테스트")
        void createProductSuccessTest() {
            // given
            doNothing().when(productImageService).createProductImages(any(ProductEntity.class), anyList(), anyList());

            // when
            ProductDto result = sellerProductService.createProduct(sellerEntity, requestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getProductName()).isEqualTo("Test Product");

            Optional<ProductEntity> savedProduct = productRepository.findById(result.getProductId());
            assertThat(savedProduct).isPresent();
            assertThat(savedProduct.get().getProductName()).isEqualTo("Test Product");
        }

        @Test
        @DisplayName("유효하지 않은 카테고리가 입력되면 Exception이 발생합니다.")
        void createProductFailTest_invalidCategory() {
            // given
            requestDto.setCategoryId(2L);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createProduct(sellerEntity, requestDto)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.INVALID_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("예약 요청이 true일 때 예약 시간이 누락되면 Exception이 발생합니다.")
        void createProductFailTest_reservationTime() {
            // given
            requestDto.setReservationTime(null);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createProduct(sellerEntity, requestDto)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.INVALID_RESERVATION.getMessage());
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 6, 7, 8, 9})
        @DisplayName("예약 요청이 true일 때 예약 상태가 유효하지 않으면 Exception이 발생합니다.")
        void createProductFailTest_reservationStatus(long reservationStatus) {
            // given
            requestDto.setReservationStatus(reservationStatus);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createProduct(sellerEntity, requestDto)
            ).isInstanceOf(IllegalArgumentException.class).hasMessage(ProductMessage.INVALID_STATUS.getMessage());
        }
    }
}
