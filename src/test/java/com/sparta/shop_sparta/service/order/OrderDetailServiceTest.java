package com.sparta.shop_sparta.service.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.shop_sparta.constant.order.OrderResponseMessage;
import com.sparta.shop_sparta.constant.product.ProductStatus;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.product.ProductDto;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.domain.entity.product.CategoryEntity;
import com.sparta.shop_sparta.domain.entity.product.ProductEntity;
import com.sparta.shop_sparta.exception.OrderException;
import com.sparta.shop_sparta.repository.OrderDetailRepository;
import com.sparta.shop_sparta.service.product.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {
    @InjectMocks
    OrderDetailServiceImpl orderDetailService;
    @Mock
    private ProductService productService;
    @Mock
    private OrderDetailRepository orderDetailRepository;

    ProductEntity productEntity;
    OrderEntity orderEntity;
    List<OrderDetailDto> orderDetailDtoList;

    @BeforeEach
    void init() {
        productEntity = ProductEntity.builder().amount(1000L).price(200000L).productId(1L).categoryEntity(
                CategoryEntity.builder().categoryId(1L).categoryName("아이템").build()).productStatus(ProductStatus.ON_SALE).build();
        orderEntity = OrderEntity.builder().orderId(1L).build();
        OrderDetailDto orderDetailDto = OrderDetailDto.builder().amount(10L).productDto(ProductDto.builder().productId(1L).build()).build();

        orderDetailDtoList = new ArrayList<>();
        orderDetailDtoList.add(orderDetailDto);
    }

    @Nested
    @DisplayName("[주문 상세 추가 테스트]")
    class AddOrderDetailTest {

        @Test
        @DisplayName("주문 상세 추가 성공")
        void addOrderDetailTest() {
            // given
            when(productService.getProductEntity(anyLong())).thenReturn(productEntity);
            when(orderDetailRepository.saveAll(any())).thenReturn(anyList());

            Long totalAmount = 0L;

            for(OrderDetailDto orderDetailDto : orderDetailDtoList) {
                totalAmount += orderDetailDto.getAmount();
            }

            // when
            Long totalPrice = orderDetailService.addOrder(orderEntity, orderDetailDtoList);
            // then

            assertThat(totalPrice).isEqualTo(productEntity.getPrice() * totalAmount);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -1, -2})
        @DisplayName("주문 수량이 0 이하인 경우 Exception이 발생합니다.")
        void addOrderDetailAmountFailTest(Long amount) {
            // given
            when(productService.getProductEntity(anyLong())).thenReturn(productEntity);
            orderDetailDtoList.clear();
            orderDetailDtoList.add(OrderDetailDto.builder().productDto(ProductDto.builder().productId(1L).amount(10L).build()).amount(amount).build());

            // when then
            assertThatThrownBy(
                    () -> orderDetailService.addOrder(orderEntity, orderDetailDtoList)
            ).isInstanceOf(OrderException.class).hasMessage(OrderResponseMessage.OUT_OF_STOCK.getMessage());
        }

        @Test
        @DisplayName("주문 수량이 0 이하인 경우 Exception이 발생합니다.")
        void addOrderDetailStockFailTest() {
            // given
            when(productService.getProductEntity(anyLong())).thenReturn(productEntity);
            orderDetailDtoList.clear();
            orderDetailDtoList.add(OrderDetailDto.builder().amount(1001L).productDto(ProductDto.builder().productId(1L).amount(1000L).build()).build());

            // when then
            assertThatThrownBy(
                    () -> orderDetailService.addOrder(orderEntity, orderDetailDtoList)
            ).isInstanceOf(OrderException.class).hasMessage(OrderResponseMessage.OUT_OF_STOCK.getMessage());
        }
    }

    @Test
    void cancelOrderTest(){
        // given
        OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder().amount(10L).productEntity(productEntity).build();
        List<OrderDetailEntity> orderDetailEntityList = new ArrayList<>();
        orderDetailEntityList.add(orderDetailEntity);

        when(orderDetailRepository.findByOrderEntity(orderEntity)).thenReturn(orderDetailEntityList);

        // when
        orderDetailService.cancelOrder(orderEntity);

        // then
        assertThat(productEntity.getAmount()).isEqualTo(1010L);
    }
}
