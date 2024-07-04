package com.sparta.shop_sparta.service.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.constant.product.ProductImageType;
import com.sparta.shop_sparta.constant.product.ProductMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductImageService productImageService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    ProductRequestDto productRequestDto;
    ProductEntity productEntity;
    MemberEntity memberEntity;
    MemberEntity sellerEntity;
    CategoryEntity categoryEntity;

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
                .amount(20L).categoryEntity(categoryEntity).sellerEntity(sellerEntity).productDetail("메롱").productName("지웅이꺼").build();
    }

    @Nested
    @DisplayName("[상품 등록 테스트]")
    class CreateProductTest {
        @Test
        @DisplayName("상품 등록 성공")
        void createProductSuccessTest() {
            // given
            when(categoryRepository.findById(productRequestDto.getCategoryId())).thenReturn(Optional.of(categoryEntity));
            when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
            doNothing().when(productImageService).createProductImages(any(ProductEntity.class), anyList(), anyList());
            // when

            ResponseEntity<?> response = productService.createProduct(memberEntity, productRequestDto);
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 입력 시 Exception이 발생합니다.")
        void createProductCategoryFailTest() {
            // given
            when(categoryRepository.findById(productRequestDto.getCategoryId())).thenReturn(Optional.ofNullable(null));
            // when then
            assertThatThrownBy(
                    () -> productService.createProduct(memberEntity, productRequestDto)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.INVALID_CATEGORY.getMessage());
        }

        @Test
        @DisplayName("이미지 저장에서 예외 발생 시 bad Request를 반환합니다.")
        void createProductImageFailTest() {
            // given
            when(categoryRepository.findById(productRequestDto.getCategoryId())).thenReturn(Optional.of(categoryEntity));
            when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
            doThrow(new RuntimeException()).when(productImageService).createProductImages(any(ProductEntity.class), anyList(), anyList());

            // when
            ResponseEntity<?> response = productService.createProduct(memberEntity, productRequestDto);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

            // when
            ResponseEntity<?> response = productService.updateProduct(memberEntity, productRequestDto);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("업데이트 성공 테스트")
        void updateProductIdFailTest() {
            // given
            when(productRepository.findById(productRequestDto.getProductId())).thenReturn(Optional.ofNullable(null));

            // when then
            assertThatThrownBy(
                    ()-> productService.updateProduct(memberEntity, productRequestDto)
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
                    ()-> productService.updateProduct(memberEntity, productRequestDto)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @Test
        @DisplayName("상품 삭제 성공 테스트")
        void deleteProductSuccessTest() {
            // given
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

            // when
            ResponseEntity<?> response = productService.deleteProduct(memberEntity, 1L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
                    ()-> productService.deleteProduct(memberEntity, 1L)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }
    }

    @Nested
    @DisplayName("[상품 조회 테스트]")
    class GetProductTest{
        @Test
        @DisplayName("조회 성공")
        void getProductSuccessTest(){
            // given
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productImageService.getProductImages(any())).thenReturn(anyList());

            // when
            ProductDto productDto = productService.getProductDto(1L);

            // then
            assertThat(productDto.getProductName()).isEqualTo(productEntity.getProductName());
            assertThat(productDto.getProductDetail()).isEqualTo(productEntity.getProductDetail());
            assertThat(productDto.getPrice()).isEqualTo(productEntity.getPrice());
        }

        @Test
        @DisplayName("이미지 생성에 실패하면 Exception이 발생합니다.")
        void getProductFailTest(){
            // given
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productImageService.getProductImages(any())).thenThrow(new ProductException(ProductMessage.FAIL_IO_IMAGE.getMessage()));

            // when then
            assertThatThrownBy(
                    ()->productService.getProductDto(1L)
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
                    .imagePath("어딘가!").imageOrdering((byte) 1).build());

            when(productRepository.findAll()).thenReturn(productEntities);
            when(productImageService.getAllProductImages()).thenReturn(productImageEntities);

            // when
            ResponseEntity<?> response = productService.getAllProducts();

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("")
        void getAllProductsFailTest(){
            // given
            when(productImageService.getAllProductImages()).thenThrow(new ProductException(ProductMessage.FAIL_IO_IMAGE.getMessage()));

            // when then
            assertThatThrownBy(
                    ()-> productService.getAllProducts()
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.FAIL_IO_IMAGE.getMessage());
        }
    }
}
