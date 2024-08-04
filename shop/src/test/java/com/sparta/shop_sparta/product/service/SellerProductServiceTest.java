package com.sparta.shop_sparta.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.member.MemberRole;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.member.repository.MemberRepository;
import com.sparta.shop_sparta.product.domain.dto.CategoryDto;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.dto.ProductRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ProductStatusRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationRequestDto;
import com.sparta.shop_sparta.product.domain.dto.ReservationResponseDto;
import com.sparta.shop_sparta.product.domain.dto.StockRequestDto;
import com.sparta.shop_sparta.product.domain.dto.StockResponseDto;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.ReservationEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.product.repository.CategoryRepository;
import com.sparta.shop_sparta.product.repository.ProductImageRepository;
import com.sparta.shop_sparta.product.repository.ProductRepository;
import com.sparta.shop_sparta.product.repository.ReservationRepository;
import com.sparta.shop_sparta.product.repository.StockRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SellerProductService sellerProductService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private CategoryService categoryService;

    // mock으로 안쓰면 s3에 실제로 저장된다
    // 돈 많으면 풀어라...
    @MockBean
    private ProductImageService productImageService;

    private MemberEntity sellerEntity;
    private CategoryDto categoryDto;
    private StockEntity stockEntity;

    @BeforeEach
    void init() {
        jdbcTemplate.execute("ALTER TABLE member AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE product AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE reservation AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE category AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE stock AUTO_INCREMENT = 1");

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
        categoryDto = categoryService.addCategory(CategoryDto.builder().categoryName("ㅇㅇㅇ").build());

        long statusIndex = 0;

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
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

            ProductEntity product = productRepository.save(productEntity);
            stockRepository.save(StockEntity.builder()
                            .productEntity(product)
                            .amount(100L)
                    .build());
            reservationRepository.save(
                    ReservationEntity.builder()
                            .productEntity(productEntity)
                            .completed(false)
                            .reserveStatus(ProductStatus.NOT_PUBLISHED)
                            .reservationTime(LocalDateTime.now())
                    .build());
        }
    }

    @AfterEach
    void clear() {
        stockRepository.deleteAll();
        productImageRepository.deleteAll();
        reservationRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("[통합 테스트] 판매자 등록 상품 조회 테스트")
    class GetProductTest {
        @Test
        @DisplayName("판매자 본인 상품 전체 조회 성공 테스트")
        void getProductSuccessTest() {
            // when
            List<ProductDto> products = sellerProductService.getSellerProducts(sellerEntity, 1, 10);

            // then
            assertThat(products.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("유효하지 않은 페이지가 입력되면 Exception이 발생합니다.")
        void getProductFailTest_invalidPage() {
            // when then
            assertThatThrownBy(
                    () -> sellerProductService.getSellerProducts(sellerEntity, 0, 10)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("deleted가 true인 데이터는 조회되지 않습니다.")
        void deleteProductSuccessTest() {
            // given
            List<ProductDto> deleteProducts = sellerProductService.getSellerProducts(sellerEntity, 1, 2);

            for (ProductDto productDto : deleteProducts) {
                sellerProductService.deleteProduct(sellerEntity, productDto.getProductId());
            }

            // when
            List<ProductDto> products = sellerProductService.getSellerProducts(sellerEntity, 1, 10);

            // then
            assertThat(products.size()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("상품 등록 테스트")
    class CreateProductTest {
        private ProductRequestDto requestDto;

        @BeforeEach
        void init() {
            requestDto = new ProductRequestDto();
            requestDto.setProductName("Test Product");
            requestDto.setProductDetail("Test Detail");
            requestDto.setCategoryId(categoryDto.getCategoryId());
            requestDto.setAmount(10L);
            requestDto.setPrice(10000L);
            List<ReservationRequestDto> reservationRequestDtos = new ArrayList<>();

            ProductDto forUpdate = sellerProductService.getSellerProducts(sellerEntity,1,1).get(0);

            reservationRequestDtos.add(ReservationRequestDto.builder()
                            .productId(forUpdate.getProductId())
                            .reservationTime(LocalDateTime.now())
                            .reservationStatus(3L)
                            .build());

            requestDto.setReservations(reservationRequestDtos);
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
            requestDto.getReservations().get(0).setReservationTime(null);

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
            requestDto.getReservations().get(0).setReservationStatus(reservationStatus);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createProduct(sellerEntity, requestDto)
            ).isInstanceOf(IllegalArgumentException.class).hasMessage(ProductMessage.INVALID_STATUS.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class updateProductTest {
        private ProductRequestDto requestDto;
        private MemberEntity otherSeller;

        @BeforeEach
        void init() {
            requestDto = new ProductRequestDto();
            requestDto.setProductName("Changed Product");
            requestDto.setProductDetail("Changed Detail");
            requestDto.setCategoryId(categoryDto.getCategoryId());
            requestDto.setProductStatus(3L);
            requestDto.setAmount(0L);
            requestDto.setPrice(10000L);

            List<ReservationRequestDto> reservationRequestDtos = new ArrayList<>();

            ProductDto forUpdate = sellerProductService.getSellerProducts(sellerEntity,1,1).get(0);

            reservationRequestDtos.add(
                    ReservationRequestDto.builder()
                            .reservationId(forUpdate.getProductId())
                            .productId(forUpdate.getProductId())
                            .reservationTime(LocalDateTime.now())
                            .reservationStatus(3L)
                    .build())
            ;

            reservationRepository.findAllByProductEntity(ProductEntity.builder().productId(forUpdate.getProductId()).build());

            requestDto.setProductId(forUpdate.getProductId());
            requestDto.setReservations(reservationRequestDtos);

            otherSeller = MemberEntity.builder()
                    .memberId(2L)
                    .build();
        }
        
        @Test
        @DisplayName("상품 전체 정보를 수정합니다.")
        void updateProductSuccessTest() {
            // when
            ProductDto productDto = sellerProductService.updateProduct(sellerEntity, requestDto, requestDto.getProductId());

            // then
            assertThat(productDto.getProductName()).isEqualTo("Changed Product");
            assertThat(productDto.getProductDetail()).isEqualTo("Changed Detail");
            assertThat(productDto.getReservationResponseDtoList().get(0).getReservationTime())
                    .isEqualTo(requestDto.getReservations().get(0).getReservationTime());
        }

        @Test
        @DisplayName("다른 사람이 등록한 상품을 수정하려고 하면 Exception이 발생합니다.")
        void updateProductFailTest_unAuthorized() {
            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateProduct(otherSeller, requestDto, requestDto.getProductId())
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @Test
        @DisplayName("유효하지 않은 수량을 입력하면 Exception이 발생합니다.")
        void updateProductFailTest_invalidAmount() {
            //given
            requestDto.setAmount(-1L);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateProduct(sellerEntity, requestDto, requestDto.getProductId())
            ).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("유효하지 않은 예약 시간을 입력하면 Exception이 발생합니다.")
        void updateProductFailTest_invalidReservationTime() {
            //given
            requestDto.getReservations().get(0).setReservationTime(null);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateProduct(sellerEntity, requestDto, requestDto.getProductId())
            ).isInstanceOf(DataIntegrityViolationException.class);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 6, 7, 8, 9})
        @DisplayName("유효하지 않은 예약 시간을 입력하면 Exception이 발생합니다.")
        void updateProductFailTest_invalidReservationStatus(Long reservationStatus) {
            //given
            requestDto.getReservations().get(0).setReservationStatus(reservationStatus);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateProduct(sellerEntity, requestDto, requestDto.getProductId())
            ).isInstanceOf(IllegalArgumentException.class).hasMessage(ProductMessage.INVALID_STATUS.getMessage());
        }

        @Test
        @DisplayName("재고 업데이트에 성공합니다.")
        void updateStockSuccessTest() {
            // given
            ProductEntity product = productRepository.findAll().getFirst();
            StockRequestDto stockRequestDto = StockRequestDto.builder()
                    .amount(10L)
                    .productId(product.getProductId())
                    .build();

            // when
            StockResponseDto stockResponseDto = sellerProductService.updateStock(sellerEntity, stockRequestDto);

            // then
            assertThat(stockResponseDto.getProductId()).isEqualTo(stockRequestDto.getProductId());
            assertThat(stockResponseDto.getAmount()).isEqualTo(stockRequestDto.getAmount());
        }

        @Test
        @DisplayName("판매자 정보가 다르면 Exception이 발생합니다.")
        void updateStockFailTest_otherSeller() {
            // given
            ProductEntity product = productRepository.findAll().getFirst();

            StockRequestDto stockRequestDto = StockRequestDto.builder()
                    .amount(10L)
                    .productId(product.getProductId())
                    .build();

            MemberEntity otherSeller = MemberEntity.builder()
                    .memberId(2L)
                    .build();

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateStock(otherSeller, stockRequestDto)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @Test
        @DisplayName("0보다 작은 재고가 입력되면 Exception이 발생합니다.")
        void updateStockFailTest_InvalidAmount() {
            // given
            ProductEntity product = productRepository.findAll().getFirst();

            StockRequestDto stockRequestDto = StockRequestDto.builder()
                    .amount(-1L)
                    .productId(product.getProductId())
                    .build();

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateStock(sellerEntity, stockRequestDto)
            ).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("상품 상태 변경 성공")
        void updateProductStatusSuccessTest() {
            // given
            ProductStatusRequestDto productStatusRequestDto = ProductStatusRequestDto.builder()
                    .productStatus(4L)
                    .productId(1L)
                    .build();

            // when
            sellerProductService.updateProductStatus(sellerEntity, productStatusRequestDto);
            ProductEntity productEntity = productRepository.findById(1L).get();

            //then
            assertThat(productEntity.getProductId()).isEqualTo(productStatusRequestDto.getProductId());
            assertThat(productEntity.getProductStatus()).isEqualTo(ProductStatus.of(productStatusRequestDto.getProductStatus()));
        }

        @Test
        @DisplayName("다른 판매자의 상품을 변경하려하면 Exception이 발생합니다.")
        void updateProductStatusFailTest_otherSeller() {
            // given
            ProductStatusRequestDto productStatusRequestDto = ProductStatusRequestDto.builder()
                    .productStatus(4L)
                    .productId(1L)
                    .build();

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateProductStatus(otherSeller, productStatusRequestDto)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 6, 7, 8, 9})
        @DisplayName("유효하지 않은 상태 코드를 입력하면 Exception이 발생합니다.")
        void updateProductStatusFailTest_InvalidStatus(long productStatus) {
            // given
            ProductStatusRequestDto productStatusRequestDto = ProductStatusRequestDto.builder()
                    .productStatus(productStatus)
                    .productId(1L)
                    .build();

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.updateProductStatus(sellerEntity, productStatusRequestDto)
            ).isInstanceOf(IllegalArgumentException.class).hasMessage(ProductMessage.INVALID_STATUS.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 상태 예약 테스트")
    class ReservationTest{
        ReservationRequestDto reservationRequestDto;
        MemberEntity otherSeller;

        @BeforeEach
        void setReuqest(){
            reservationRequestDto = ReservationRequestDto.builder()
                    .reservationTime(LocalDateTime.now())
                    .reservationStatus(4L)
                    .build();

            otherSeller = MemberEntity.builder()
                    .memberId(2L)
                    .build();
        }

        @Test
        @DisplayName("예약 생성 성공")
        void createReservationSuccessTest() {
            // given
            Long productId = 1L;
            reservationRequestDto.setProductId(productId);

            // when
            sellerProductService.createReservation(sellerEntity, reservationRequestDto);
            List<ReservationResponseDto> reservations = reservationService.getReservationsByProductEntity(sellerProductService.getProductEntity(productId));
            ReservationResponseDto reservationResponseDto = reservations.get(1);

            // then
            // 초기값 1개 있으므로 추가되면 2
            assertThat(reservations.size()).isEqualTo(2);
            assertThat(reservationResponseDto.getProductId()).isEqualTo(productId);
            assertThat(reservationResponseDto.getReservationTime()).isCloseTo(reservationRequestDto.getReservationTime(), within(100, ChronoUnit.MILLIS));
            assertThat(reservationResponseDto.getReserveStatus()).isEqualTo(ProductStatus.of(reservationRequestDto.getReservationStatus()));
        }

        @Test
        @DisplayName("다른 판매자의 상품에 예약 시도를 하는 경우 Exception이 발생합니다.")
        void createReservationFailTest_otherSeller() {
            // given
            Long productId = 1L;
            reservationRequestDto.setProductId(productId);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createReservation(otherSeller, reservationRequestDto)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @Test
        @DisplayName("시간 값이 null이면 Exception이 발생합니다.")
        void createReservationFailTest_invalidReservationTime() {
            // given
            Long productId = 1L;
            reservationRequestDto.setProductId(productId);
            reservationRequestDto.setReservationTime(null);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createReservation(sellerEntity, reservationRequestDto)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.INVALID_RESERVATION.getMessage());
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 6, 7, 8, 9})
        @DisplayName("유효하지 않은 상태 코드가 입력되면 발생합니다.")
        void createReservationFailTest_invalidReservationStatus(long productStatus) {
            // given
            Long productId = 1L;
            reservationRequestDto.setProductId(productId);
            reservationRequestDto.setReservationStatus(productStatus);

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createReservation(sellerEntity, reservationRequestDto)
            ).isInstanceOf(IllegalArgumentException.class).hasMessage(ProductMessage.INVALID_STATUS.getMessage());
        }

        @Test
        @DisplayName("예약 수정 성공 테스트")
        void updateReservationSuccessTest() {
            // given
            Long reservationId = 1L;
            Long productId = 1L;
            reservationRequestDto.setReservationId(reservationId);
            reservationRequestDto.setProductId(productId);
            LocalDateTime updateTime = LocalDateTime.now();
            reservationRequestDto.setReservationTime(updateTime);

            List<ReservationRequestDto> reservations = new ArrayList<>();
            reservations.add(reservationRequestDto);

            ProductEntity productEntity = productRepository.findById(productId).get();

            // when
            ReservationResponseDto response = sellerProductService.updateReservations(sellerEntity, productEntity, reservations).getFirst();

            // then
            assertThat(response.getReservationId()).isEqualTo(reservationId);
            assertThat(response.getProductId()).isEqualTo(productId);
            assertThat(response.getReservationTime()).isCloseTo(updateTime, within(100, ChronoUnit.MILLIS));
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest{
        @Test
        @DisplayName("삭제 성공 테스트")
        void deleteProductSuccessTest() {
            // given
            Long productId = 1L;

            // when
            sellerProductService.deleteProduct(sellerEntity, productId);
            ProductDto productDto = sellerProductService.getSellerProducts(sellerEntity, 1, 1).getFirst();

            // then
            assertThat(productDto.getProductId()).isNotEqualTo(productDto);
        }

        @Test
        @DisplayName("다른 판매자의 상품을 삭제하려고 하면 Exception이 발생합니다.")
        void deleteProductFailTest_otherSeller() {
            // given
            Long productId = 1L;

            MemberEntity otherSeller = MemberEntity.builder()
                    .memberId(2L)
                    .build();

            // when then
            assertThatThrownBy(
                    () -> sellerProductService.deleteProduct(otherSeller, productId)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }
    }
}
