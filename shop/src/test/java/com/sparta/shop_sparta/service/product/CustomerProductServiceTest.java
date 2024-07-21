package com.sparta.shop_sparta.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.common.constant.member.MemberRole;
import com.sparta.common.constant.product.ProductImageType;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.dto.product.ProductImageDto;
import com.sparta.shop_sparta.domain.dto.product.ProductRequestDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CustomerProductServiceTest {
    @InjectMocks
    private CustomerProductService customerProductService;
    @Mock
    private LocalStorageImageService localStorageImageService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockService stockService;
    @Mock
    private ReservationService reservationService;

    ProductRequestDto productRequestDto;
    ProductEntity productEntity;
    MemberEntity memberEntity;
    MemberEntity sellerEntity;
    CategoryEntity categoryEntity;
    StockEntity stockEntity;

    @BeforeEach
    void init() {
        categoryEntity = CategoryEntity.builder().categoryName("아이템").categoryId(1L).build();
        memberEntity = MemberEntity.builder().memberId(1L).memberName("지웅이").email("sin9158@naver.com")
                .role(MemberRole.BASIC).loginId("sin9158").password("test").phoneNumber("010-2720-9158").build();
        sellerEntity = MemberEntity.builder().memberId(1L).memberName("장사꾼지웅이").email("shin9158@gmail.com")
                .role(MemberRole.SELLER).loginId("shin91588").password("test12").phoneNumber("010-2720-9151").build();
        productRequestDto = ProductRequestDto.builder().productName("지웅이꺼").productDetail("메롱이다").productStatus(1L)
                .price(100000L).productDetailImages(new ArrayList<>()).productThumbnails(new ArrayList<>()).amount(20L).build();
        productEntity = ProductEntity.builder().productId(1L).productStatus(ProductStatus.WAITING).price(100000L)
                .categoryEntity(categoryEntity).sellerEntity(sellerEntity).productDetail("메롱").productName("지웅이꺼").build();
        stockEntity = StockEntity.builder().amount(20L).productEntity(productEntity).build();
    }

    @Nested
    @DisplayName("[상품 조회 테스트]")
    class GetProductTest{
        @Test
        @DisplayName("조회 성공")
        void getProductSuccessTest(){
            // given
            //when(productImageService.getProductImages(any())).thenReturn(anyList());
            //when(stockService.getStock(any())).thenReturn(anyLong());
            when(reservationService.getReservationsByProductEntity(any(ProductEntity.class))).thenReturn(new ArrayList<>());
            // when
            ProductDto productDto = customerProductService.getProductDto(productEntity);

            // then
            assertThat(productDto.getProductName()).isEqualTo(productEntity.getProductName());
            assertThat(productDto.getProductDetail()).isEqualTo(productEntity.getProductDetail());
            assertThat(productDto.getPrice()).isEqualTo(productEntity.getPrice());
        }

        @Test
        @DisplayName("이미지 생성에 실패하면 Exception이 발생합니다.")
        void getProductFailTest(){
            // given
            when(localStorageImageService.getProductImages(any())).thenThrow(new ProductException(ProductMessage.FAIL_IO_IMAGE));

            // when then
            assertThatThrownBy(
                    ()->customerProductService.getProductDto(productEntity)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.FAIL_IO_IMAGE.getMessage());
        }

        @Test
        @DisplayName("전체 상품 조회 성공")
        void getAllProductsSuccessTest(){
            // given
            List<ProductEntity> productEntities = new ArrayList<>();
            productEntities.add(productEntity);

            List<ProductImageDto> productImageEntities = new ArrayList<>();
            productImageEntities.add(ProductImageDto.builder().productImageType(ProductImageType.HEADER).productId(1L)
                    .imageData("어딘가!").imageOrdering((byte) 1).build());

            when(productRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
            when(localStorageImageService.getProductByPage(anyList())).thenReturn(new ArrayList<>());
            //when(productRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            // when
            customerProductService.getAllProducts(1, 10);

            // then
            verify(productRepository).findAll(any(Pageable.class));
            verify(localStorageImageService).getProductByPage(anyList());
        }

        @Test
        @DisplayName("이미 로드에 실패하면 Exception이 발생합니다.")
        void getAllProductsFailTest(){
            // given
            when(localStorageImageService.getProductByPage(anyList())).thenThrow(new ProductException(ProductMessage.FAIL_IO_IMAGE));
            when(productRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            // when then
            assertThatThrownBy(
                    ()-> customerProductService.getAllProducts(1, 10)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.FAIL_IO_IMAGE.getMessage());
        }
    }
}
