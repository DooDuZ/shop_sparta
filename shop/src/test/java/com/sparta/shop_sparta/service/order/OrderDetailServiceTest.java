package com.sparta.shop_sparta.service.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.order.OrderResponseMessage;
import com.sparta.common.constant.product.ProductStatus;
import com.sparta.common.exception.OrderException;
import com.sparta.shop_sparta.order.domain.dto.OrderDetailRequestDto;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderDetailEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import com.sparta.shop_sparta.order.service.OrderDetailService;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.order.repository.OrderDetailRepository;
import com.sparta.shop_sparta.product.service.ProductService;
import com.sparta.shop_sparta.product.service.StockService;
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
    OrderDetailService orderDetailService;
    @Mock
    private ProductService productService;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private StockService stockService;

    ProductEntity productEntity;
    OrderEntity orderEntity;
    List<OrderDetailRequestDto> orderDetailDtoList;
    StockEntity stockEntity;

    @BeforeEach
    void init() {
        productEntity = ProductEntity.builder().price(200000L).productId(1L).categoryEntity(
                CategoryEntity.builder().categoryId(1L).categoryName("아이템").build()).sellerEntity(
                MemberEntity.builder().build()).productStatus(ProductStatus.ON_SALE).build();
        orderEntity = OrderEntity.builder().orderId(1L).build();
        stockEntity = StockEntity.builder().amount(1000L).productEntity(productEntity).build();

        OrderDetailRequestDto orderDetailDto = OrderDetailRequestDto.builder().amount(10L).productId(1L).build();

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
            when(stockService.getStockByProductId(anyLong())).thenReturn(stockEntity);
            when(stockService.getStockInRedis(anyLong())).thenReturn(stockEntity.getAmount());

            Long totalAmount = 0L;

            for(OrderDetailRequestDto orderDetailDto : orderDetailDtoList) {
                totalAmount += orderDetailDto.getAmount();
            }

            // when
            List<OrderDetailEntity> orderDetails = orderDetailService.addOrder(orderEntity, orderDetailDtoList);

            Long totalPrice = 0L;

            for (OrderDetailEntity orderDetailEntity : orderDetails) {
                totalPrice += orderDetailEntity.getAmount() * orderDetailEntity.getProductEntity().getPrice();
            }

            // then
            assertThat(totalPrice).isEqualTo(productEntity.getPrice() * totalAmount);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -1, -2})
        @DisplayName("주문 수량이 0 이하인 경우 Exception이 발생합니다.")
        void addOrderDetailAmountFailTest(Long amount) {
            // given
            when(stockService.getStockByProductId(anyLong())).thenReturn(stockEntity);
            orderDetailDtoList.clear();
            orderDetailDtoList.add(OrderDetailRequestDto.builder().productId(1L).amount(amount).build());

            // when then
            assertThatThrownBy(
                    () -> orderDetailService.addOrder(orderEntity, orderDetailDtoList)
            ).isInstanceOf(OrderException.class).hasMessage(OrderResponseMessage.INVALID_AMOUNT.getMessage());
        }

        @Test
        @DisplayName("주문 수량이 0 이하인 경우 Exception이 발생합니다.")
        void addOrderDetailStockFailTest() {
            // given
            when(stockService.getStockByProductId(anyLong())).thenReturn(stockEntity);
            orderDetailDtoList.clear();
            orderDetailDtoList.add(OrderDetailRequestDto.builder().amount(1001L).productId(1L).build());

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
        when(stockService.getStockEntity(any(ProductEntity.class))).thenReturn(stockEntity);

        // when
        orderDetailService.cancelOrder(orderEntity);

        // then
        assertThat(stockEntity.getAmount()).isEqualTo(1010L);
    }
}
