package com.sparta.shop_sparta.service.cart;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.member.MemberRole;
import com.sparta.common.constant.product.ProductMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.ProductException;
import com.sparta.shop_sparta.domain.dto.cart.CartDetailResponseDto;
import com.sparta.shop_sparta.domain.dto.cart.CartDto;
import com.sparta.shop_sparta.domain.dto.cart.CartRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.domain.entity.product.StockEntity;
import com.sparta.shop_sparta.repository.memoryRepository.CartRedisRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import com.sparta.shop_sparta.service.product.StockService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @InjectMocks
    CartService cartService;
    @Mock
    private CartRedisRepository cartRedisRepository;
    @Mock
    private ProductService productService;
    @Mock
    private StockService stockService;

    MemberEntity memberEntity;
    CartRequestDto cartRequestDto;
    ProductEntity productEntity;
    Map<Long, Long> cartInfo;
    List<OrderDetailDto> orderDetails;
    StockEntity stockEntity;

    @BeforeEach
    void init(){
        memberEntity = MemberEntity.builder().memberId(1L).email("sin9158@naver.com").role(MemberRole.BASIC).memberName("지웅이")
                .loginId("sin9158").password("test123!@#").phoneNumber("010-2720-9158").build();
        cartRequestDto = CartRequestDto.builder().productId(1L).amount(10L).build();
        productEntity = ProductEntity.builder().productId(1L).price(10000L).sellerEntity(memberEntity)
                .categoryEntity(new CategoryEntity()).productName("지웅이").productDetail("없어요")
                .productStatus(ProductStatus.ON_SALE).build();
        stockEntity = StockEntity.builder().productEntity(productEntity).amount(1000L).build();

        cartInfo = new HashMap<>();
        cartInfo.put(1L, 10L);

        orderDetails = new ArrayList<>();
        OrderDetailDto orderDetailDto = OrderDetailDto.builder().productDto(productEntity.toDto()).orderId(1L).amount(10L).orderDetailId(1L).build();
        orderDetails.add(orderDetailDto);
    }

    @Test
    @DisplayName("장바구니 생성 성공 테스트")
    void createCartSuccessTest(){
        // given
        MemberEntity member = memberEntity;
        // when then
        assertDoesNotThrow(
                () -> cartService.createCart(member)
        );
    }

    @Nested
    @DisplayName("[상품 추가 테스트]")
    class addToCartTest{
        @Test
        @DisplayName("상품 추가 성공")
        void addToCartSuccessTest(){
            // given
            when(cartRedisRepository.hasKey(anyLong())).thenReturn(true);
            when(productService.getProductEntity(1L)).thenReturn(productEntity);
            when(stockService.getStockEntity(any(ProductEntity.class))).thenReturn(stockEntity);
            doNothing().when(cartRedisRepository).saveWithDuration(anyLong(), anyLong(), anyLong());
            // when
            ProductDto productDto = cartService.addProductToCart(memberEntity, cartRequestDto);
            // then
            assertThat(productDto.getProductId()).isEqualTo(productEntity.getProductId());
            assertThat(productDto.getProductDetail()).isEqualTo(productEntity.getProductDetail());
            assertThat(productDto.getPrice()).isEqualTo(productEntity.getPrice());
            assertThat(productDto.getAmount()).isEqualTo(stockEntity.getAmount());
        }

        @Test
        @DisplayName("장바구니가 없는 경우 장바구니 생성 후 상품을 추가 합니다.")
        void addToCartAsNoneSuccessTest(){
            // given
            when(cartRedisRepository.hasKey(anyLong())).thenReturn(false);
            when(productService.getProductEntity(1L)).thenReturn(productEntity);
            when(stockService.getStockEntity(any(ProductEntity.class))).thenReturn(stockEntity);
            doNothing().when(cartRedisRepository).saveWithDuration(anyLong(), anyLong(), anyLong());
            // when
            ProductDto productDto = cartService.addProductToCart(memberEntity, cartRequestDto);
            // then
            assertThat(productDto.getProductId()).isEqualTo(productEntity.getProductId());
            assertThat(productDto.getProductDetail()).isEqualTo(productEntity.getProductDetail());
            assertThat(productDto.getPrice()).isEqualTo(productEntity.getPrice());
            assertThat(productDto.getAmount()).isEqualTo(stockEntity.getAmount());
        }

        @Test
        @DisplayName("존재하지 않는 상품을 담으려고 하는 경우 Exception이 발생합니다.")
        void addToCartInvalidProductIdFailTest(){
            // given
            when(productService.getProductEntity(anyLong())).thenThrow(new ProductException(ProductMessage.NOT_FOUND_PRODUCT));

            // when then
            assertThatThrownBy(
                    () -> cartService.addProductToCart(memberEntity, cartRequestDto)
            ).isInstanceOf(ProductException.class).hasMessageContaining(ProductMessage.NOT_FOUND_PRODUCT.getMessage());
        }

        @ParameterizedTest
        @EnumSource(value = ProductStatus.class, names = {"ON_SALE"}, mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("판매 중이 아닌 상품을 담으려고 하는 경우 Exception이 발생합니다.")
        void addToCartNotOnSaleFailTest(ProductStatus productStatus){
            // given
            productEntity.setProductStatus(productStatus);
            when(productService.getProductEntity(anyLong())).thenReturn(productEntity);

            // when then
            assertThatThrownBy(
                    () -> cartService.addProductToCart(memberEntity, cartRequestDto)
            ).isInstanceOf(ProductException.class).hasMessageContaining(ProductMessage.NOT_ON_SALE.getMessage());
        }

        /*
        부하 테스트 위해서 임시 주석
        @Test
        @DisplayName("담으려는 상품의 수가 재고보다 많은 경우 Exception이 발생합니다.")
        void addToCartOutOfStockFailTest(){
            // given
            stockEntity.setAmount(1L);
            when(cartRedisRepository.hasKey(anyLong())).thenReturn(true);
            when(productService.getProductEntity(1L)).thenReturn(productEntity);
            when(stockService.getStockEntity(productEntity)).thenReturn(stockEntity);

            // when then
            assertThatThrownBy(
                    () -> cartService.addProductToCart(memberEntity, cartRequestDto)
            ).isInstanceOf(ProductException.class).hasMessageContaining(ProductMessage.OUT_OF_STOCK.getMessage());
        }*/
    }

    @Nested
    @DisplayName("[상품 조회 테스트]")
    class getCartTest{
        @Test
        @DisplayName("장바구니에 상품이 있는 경우 해당 상품 목록이 조회됩니다.")
        void getCartSuccessTest(){
            // given
            List<ProductDto> productDtoList = new ArrayList<>();
            productDtoList.add(productEntity.toDto());

            when(cartRedisRepository.findCart(memberEntity.getMemberId())).thenReturn(cartInfo);
            when(productService.getProductDtoList(cartInfo)).thenReturn(productDtoList);

            // when
            CartDto cartDto = cartService.getCart(memberEntity);
            List<CartDetailResponseDto> cart = cartDto.getCartInfo();

            // then
            assertThat(cartDto.getMemberId()).isEqualTo(memberEntity.getMemberId());
            assertThat(cart.get(0).getProductDto().getProductId()).isEqualTo(productEntity.getProductId());
        }

        @Test
        @DisplayName("장바구니에 상품이 없는 경우 null이 반환 됩니다.")
        void getEmptyCartSuccessTest(){
            // given
            cartInfo = new HashMap<>();
            when(cartRedisRepository.findCart(memberEntity.getMemberId())).thenReturn(cartInfo);

            // when
            CartDto cartDto = cartService.getCart(memberEntity);

            // then
            assertThat(cartDto.getMemberId()).isEqualTo(memberEntity.getMemberId());
            assertThat(cartDto.getCartInfo()).isNull();
        }

        @Test
        @DisplayName("상품 이미지 불러오기에 실패하는 경우 Exception이 발생합니다.")
        void getCartIOFailTest(){
            // given
            cartInfo = new HashMap<>();
            when(cartRedisRepository.findCart(memberEntity.getMemberId())).thenReturn(cartInfo);
            when(cartService.getProductsIncart(cartInfo)).thenThrow(new ProductException(ProductMessage.FAIL_IO_IMAGE));

            // when then
            assertThatThrownBy(
                    () -> cartService.getCart(memberEntity)
            ).isInstanceOf(ProductException.class).hasMessage(ProductMessage.FAIL_IO_IMAGE.getMessage());
        }

        @Test
        @DisplayName("Redis에 장바구니 정보가 있다면 Map형태로 반환합니다.")
        void getCartInfoSuccessTest(){
            // given
            when(cartRedisRepository.findCart(memberEntity.getMemberId())).thenReturn(cartInfo);
            // when
            Map<Long, Long> result = cartService.getCartInfo(memberEntity);
            // then
            assertThat(result).isEqualTo(cartInfo);
        }
    }
}
