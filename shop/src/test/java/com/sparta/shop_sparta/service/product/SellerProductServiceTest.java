package com.sparta.shop_sparta.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.member.MemberRole;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.product.domain.dto.ProductDto;
import com.sparta.shop_sparta.product.domain.dto.ProductRequestDto;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.product.service.LocalStorageImageService;
import com.sparta.shop_sparta.product.service.SellerProductService;
import com.sparta.shop_sparta.product.service.StockService;
import com.sparta.shop_sparta.product.repository.CategoryRepository;
import com.sparta.shop_sparta.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SellerProductServiceTest {
    @InjectMocks
    private SellerProductService sellerProductService;
    @Mock
    private LocalStorageImageService localStorageImageService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StockService stockService;

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
    @DisplayName("[상품 등록 테스트]")
    class CreateProductTest {
        @Test
        @DisplayName("상품 등록 성공")
        void createProductSuccessTest() {
            // given
            productRequestDto = ProductRequestDto.builder().productId(1L).price(100000L)
                    .amount(20L).categoryId(1L).sellerId(1L).productDetail("메롱").productName("지웅이꺼")
                    .productThumbnails(new ArrayList<>()).productDetailImages(new ArrayList<>())
                    .build();
            when(categoryRepository.findById(productRequestDto.getCategoryId())).thenReturn(Optional.of(categoryEntity));
            when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
            doNothing().when(localStorageImageService).createProductImages(any(ProductEntity.class), anyList(), anyList());

            // when
            ProductDto productDto = sellerProductService.createProduct(memberEntity, productRequestDto);

            // then
            assertThat(productDto.getProductDetail()).isEqualTo(productRequestDto.getProductDetail());
            assertThat(productDto.getPrice()).isEqualTo(productRequestDto.getPrice());
            assertThat(productDto.getProductStatus()).isEqualTo(ProductStatus.WAITING);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 입력 시 Exception이 발생합니다.")
        void createProductCategoryFailTest() {
            // given
            when(categoryRepository.findById(productRequestDto.getCategoryId())).thenReturn(Optional.ofNullable(null));
            // when then
            assertThatThrownBy(
                    () -> sellerProductService.createProduct(memberEntity, productRequestDto)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.INVALID_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("이미지 저장 실패 시 Exception이 발생합니댜.")
        void createProductImageFailTest() {
            // given
            when(categoryRepository.findById(productRequestDto.getCategoryId())).thenReturn(Optional.of(categoryEntity));
            when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
            doThrow(new RuntimeException()).when(localStorageImageService).createProductImages(any(ProductEntity.class), anyList(), anyList());

            // when
            assertThatThrownBy(
                    () -> sellerProductService.createProduct(memberEntity, productRequestDto)
            ).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("[상품 변경 테스트]")
    class UpdateProductTest {
        @Test
        @DisplayName("업데이트 성공 테스트")
        void updateProductSuccessTest() {
            // given
            when(productRepository.findById(productRequestDto.getProductId())).thenReturn(Optional.of(productEntity));
            when(stockService.updateStock(any(ProductEntity.class), anyLong())).thenReturn(stockEntity);
            // when
            ProductDto productDto = sellerProductService.updateProduct(memberEntity, productRequestDto, productRequestDto.getProductId());

            // then
            assertThat(productDto.getProductDetail()).isEqualTo(productRequestDto.getProductDetail());
            assertThat(productDto.getPrice()).isEqualTo(productRequestDto.getPrice());
            assertThat(productDto.getProductStatus()).isEqualTo( ProductStatus.of(productRequestDto.getProductStatus()));
        }

        @Test
        @DisplayName("업데이트 성공 테스트")
        void updateProductIdFailTest() {
            // given
            when(productRepository.findById(productRequestDto.getProductId())).thenReturn(Optional.ofNullable(null));

            // when then
            assertThatThrownBy(
                    ()-> sellerProductService.updateProduct(memberEntity, productRequestDto, productRequestDto.getProductId())
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.NOT_FOUND_PRODUCT.getMessage());
        }

        @Test
        @DisplayName("업데이트 성공 테스트")
        void updateProductSellerFailTest() {
            // given
            when(productRepository.findById(productRequestDto.getProductId())).thenReturn(Optional.of(productEntity));
            memberEntity.setMemberId(2L);
            // when then
            assertThatThrownBy(
                    ()-> sellerProductService.updateProduct(memberEntity, productRequestDto, productRequestDto.getProductId())
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @Test
        @DisplayName("상품 삭제 성공 테스트")
        void deleteProductSuccessTest() {
            // given
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

            // when
            sellerProductService.deleteProduct(memberEntity, 1L);

            // then
            assertThat(productEntity.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("다른 사람의 상품을 삭제할 경우 Exception이 발생합니다.")
        void deleteProductFailTest() {
            // given
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            memberEntity.setMemberId(2L);

            // when
            assertThatThrownBy(
                    ()-> sellerProductService.deleteProduct(memberEntity, 1L)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }
    }
}
