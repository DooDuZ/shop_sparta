package com.sparta.shop_sparta.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.order.OrderResponseMessage;
import com.sparta.common.constant.order.OrderStatus;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.common.exception.OrderException;
import com.sparta.shop_sparta.order.domain.dto.OrderDetailDto;
import com.sparta.shop_sparta.order.domain.dto.OrderDetailRequestDto;
import com.sparta.shop_sparta.order.domain.dto.OrderRequestDto;
import com.sparta.shop_sparta.order.domain.dto.OrderResponseDto;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import com.sparta.shop_sparta.product.domain.entity.CategoryEntity;
import com.sparta.shop_sparta.product.domain.entity.ProductEntity;
import com.sparta.shop_sparta.product.domain.entity.StockEntity;
import com.sparta.shop_sparta.order.repository.OrderRepository;
import com.sparta.shop_sparta.cart.service.CartService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailService orderDetailService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private CartService cartService;

    @Mock
    private OrderEntity orderedEntity;
    @Mock
    private OrderRequestDto orderRequestDto;
    @Mock
    private MemberEntity memberEntity;
    @Mock
    private ProductEntity productEntity;

    private StockEntity stockEntity;

    @BeforeEach
    void init(){
        memberEntity = MemberEntity.builder().memberName("지웅이").email("sin9158@naver.com").loginId("sin9158")
                .password("testPassword1@").phoneNumber("010-2720-9158").memberId(1L).build();

        orderedEntity = OrderEntity.builder().orderId(1L).orderStatus(OrderStatus.PREPARED).orderAddr("지구 어딘가")
                .orderAddrDetail("한국 어딘가").memberEntity(memberEntity).build();

        List<OrderDetailRequestDto> orderDetails = new ArrayList<>();
        orderRequestDto = OrderRequestDto.builder().orderAddr("경기도 어딘가").orderAddrDetail("안산시 어딘가")
                .orderDetails(orderDetails).build();
        // 넣고 하는 건 말이 안되지만 테스트이므로...
        orderRequestDto.setOrderId(1L);

        productEntity = ProductEntity.builder().productId(1L).productName("ㅇㅇ").productDetail("ㅇㅇ").categoryEntity(
                CategoryEntity.builder().categoryName("dd").categoryId(1L).build()).price(10000L).sellerEntity(memberEntity).build();

        stockEntity = StockEntity.builder().productEntity(productEntity).amount(1000L).build();

        orderDetails.add(OrderDetailRequestDto.builder().amount(10L).productId(1L).build());
    }

    @Nested
    @DisplayName("[주문 생성 테스트]")
    class CreateOrderTest{
        @Test
        @DisplayName("주문 성공")
        void createOrderSuccessTest(){
            // given
            Map<Long, Long> cartInfo = new HashMap<>();
            cartInfo.put(1L, 10L);

            List<OrderDetailDto> details = new ArrayList<>();
            details.add(OrderDetailDto.builder().amount(10L).productDto(productEntity.toDto()).build());

            when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderRequestDto.toEntity());
            when(cartService.getCartInfo(any(MemberEntity.class))).thenReturn(cartInfo);
            // when(orderDetailService.addOrder(any(OrderEntity.class), anyList())).thenReturn(details);
            when(paymentService.pay(any(OrderEntity.class))).thenReturn(true);
            //doNothing().when(cartService).removeOrderedProduct(any(MemberEntity.class), anyList());

            // when
            orderService.createOrder(memberEntity, orderRequestDto);

            // then
        /*assertThat(orderResponseDto).isNotNull();
        assertThat(orderResponseDto.getOrderId()).isEqualTo(1L);
        assertThat(orderResponseDto.getOrderStatus()).isEqualTo(OrderStatus.PREPARED);
        assertThat(orderResponseDto.getOrderAddr()).isEqualTo(orderRequestDto.getOrderAddr());
        assertThat(orderResponseDto.getOrderAddrDetail()).isEqualTo(orderRequestDto.getOrderAddrDetail());
        assertThat(orderResponseDto.getMemberId()).isEqualTo(memberEntity.getMemberId());
        assertThat(orderResponseDto.getTotalPrice()).isEqualTo(productEntity.getPrice() * 10L);*/
            // Todo 부하 테스트를 위한 임시 주석 처리
            //verify(cartService).removeOrderedProduct(any(MemberEntity.class), anyList());
        }

        @Test
        @DisplayName("장바구니에 담겨있지 않은 상품이 주문 요청되면 Exception이 발생합니다.")
        void createOrderCartInfoProductFailTest(){
            // given
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderRequestDto.toEntity());
            when(cartService.getCartInfo(any(MemberEntity.class))).thenReturn(new HashMap<>());

            // when then
            assertThatThrownBy(
                    () -> orderService.createOrder(memberEntity, orderRequestDto)
            ).isInstanceOf(OrderException.class).hasMessageContaining(OrderResponseMessage.INVALID_REQUEST.getMessage());
        }

        @Test
        @DisplayName("장바구니에 담긴 수량과 요청된 수량이 다르면 Exception이 발생합니다.")
        void createOrderCartInfoAmountFailTest(){
            // given
            Map<Long, Long> cartInfo = new HashMap<>();
            cartInfo.put(1L, 5L);

            when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderRequestDto.toEntity());
            when(cartService.getCartInfo(any(MemberEntity.class))).thenReturn(cartInfo);

            // when then
            assertThatThrownBy(
                    () -> orderService.createOrder(memberEntity, orderRequestDto)
            ).isInstanceOf(OrderException.class).hasMessageContaining(OrderResponseMessage.INVALID_REQUEST.getMessage());
        }

        @Test
        @DisplayName("결제에 실패하면 Exception이 발생합니다.")
        void createOrderPaymentFailTest(){
            // given
            Map<Long, Long> cartInfo = new HashMap<>();
            cartInfo.put(1L, 10L);

            when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderRequestDto.toEntity());
            when(cartService.getCartInfo(any(MemberEntity.class))).thenReturn(cartInfo);
            when(orderDetailService.addOrder(any(OrderEntity.class), anyList())).thenReturn(anyList());

            // when then
            assertThatThrownBy(
                    () -> orderService.createOrder(memberEntity, orderRequestDto)
            ).isInstanceOf(OrderException.class).hasMessageContaining(OrderResponseMessage.FAIL_PAYMENT.getMessage());
        }
    }

    @Nested
    @DisplayName("[주문 조회 테스트]")
    class GetOrderTest{
        @Test
        @DisplayName("주문 상세 조회 성공")
        void getOrderSuccessTest(){
            // given
            Long orderId = 1L;

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderedEntity));
            when(orderDetailService.getOrderedProduct(any(OrderEntity.class))).thenReturn(new ArrayList<>());

            // when
            OrderResponseDto orderResponseDto = orderService.getOrder(memberEntity, orderId);

            // then
            assertThat(orderResponseDto.getOrderId()).isEqualTo(orderedEntity.getOrderId());
        }

        @Test
        @DisplayName("주문 번호가 잘못된 경우 Exception이 발생합니다.")
        void getOrderInvalidOrderFailTest(){
            // given
            Long orderId = 2L;

            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(null));

            // when
            assertThatThrownBy(
                    () -> orderService.getOrder(memberEntity, orderId)
            ).isInstanceOf(OrderException.class).hasMessageContaining(OrderResponseMessage.INVALID_ORDER.getMessage());
        }

        @Test
        @DisplayName("다른 회원의 주문 정보를 조회할 경우 Exception이 발생합니다.")
        void getOrderAuthorizationFailTest(){
            // given
            Long orderId = 1L;
            MemberEntity otherMemberEntity = MemberEntity.builder().memberId(2L).build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderedEntity));

            // when
            assertThatThrownBy(
                    () -> orderService.getOrder(otherMemberEntity, orderId)
            ).isInstanceOf(AuthorizationException.class).hasMessageContaining(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }
    }

    @Nested
    @DisplayName("[주문 상태 변경 테스트]")
    class UpdateOrderTest{
        @Test
        @DisplayName("주문 취소 성공")
        void cancelOrderSuccessTest(){
            // given
            Long orderId = 1L;
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderedEntity));

            // when
            OrderResponseDto orderResponseDto = orderService.cancelOrder(memberEntity, orderId);

            // then
            assertThat(orderResponseDto.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("다른 회원의 주문을 취소 요청하면 Exception이 발생합니다.")
        void cancelOrderAuthorizationFailTest(){
            // given
            Long orderId = 1L;
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderedEntity));
            MemberEntity otherMemberEntity = MemberEntity.builder().memberId(2L).build();

            // when
            assertThatThrownBy(
                    () -> orderService.cancelOrder(otherMemberEntity, orderId)
            ).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        @Test
        @DisplayName("주문 번호가 유효하지 않으면 Exception이 발생합니다.")
        void cancelOrderInvalidOrderFailTest(){
            // given
            Long orderId = 2L;
            when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(null));

            // when
            assertThatThrownBy(
                    () -> orderService.cancelOrder(memberEntity, orderId)
            ).isInstanceOf(OrderException.class).hasMessage(OrderResponseMessage.INVALID_ORDER.getMessage());
        }

        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"PREPARED"}, mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("발송 대기 상태가 아닌 주문을 취소 요청 하면 Exception이 발생합니다.")
        void cancelOrderNotPreparedFailTest(OrderStatus orderStatus){
            // given
            Long orderId = 1L;
            orderedEntity.setOrderStatus(orderStatus);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderedEntity));

            // when then
            assertThatThrownBy(
                    ()-> orderService.cancelOrder(memberEntity, orderId)
            ).isInstanceOf(OrderException.class).hasMessage(OrderResponseMessage.FAIL_CANCEL.getMessage());
        }

        @Test
        @DisplayName("반품 신청 성공")
        void returnRequestSuccessTest(){
            // given
            Long orderId = 1L;
            LocalDateTime randomDateTime = LocalDateTime.now().minusDays(1).plusSeconds((long)(Math.random() * 24 * 60 * 60));

            orderedEntity = OrderEntity.builder().orderId(1L).orderStatus(OrderStatus.DELIVERED).orderAddr("지구 어딘가")
                    .orderAddrDetail("한국 어딘가").memberEntity(memberEntity).build();

            // last modify date는 setter없음.. get만 가능
            // 호출 시 값을 임의로 지정해주기 위해서 spy객체로 만들어서 사용
            OrderEntity spyOrderEntity = Mockito.spy(orderedEntity);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(spyOrderEntity));
            when(spyOrderEntity.getLastModifyDate()).thenReturn(randomDateTime);

            // when
            OrderResponseDto orderResponseDto = orderService.requestReturn(memberEntity, orderId);

            // then
            assertThat(orderResponseDto.getOrderStatus()).isEqualTo(OrderStatus.RETURN_REQUESTED);
        }

        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"DELIVERED"}, mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("배송 완료 상태가 아닐 때 반품 요청 시 Exception이 발생합니다.")
        void returnRequestOrderStatusFailTest(OrderStatus orderStatus){
            // given
            Long orderId = 1L;

            orderedEntity = OrderEntity.builder().orderId(1L).orderStatus(orderStatus).orderAddr("지구 어딘가")
                    .orderAddrDetail("한국 어딘가").memberEntity(memberEntity).build();

            // last modify date는 setter없음.. get만 가능

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderedEntity));

            // when then
            assertThatThrownBy(
                    ()-> orderService.requestReturn(memberEntity, orderId)
            ).isInstanceOf(OrderException.class).hasMessage(OrderResponseMessage.FAIL_REQUEST_RETURN.getMessage());
        }

        @Test
        @DisplayName("배송 완료 후 24시간 경과했다면 반품 요청 시 Exception이 발생합니다.")
        void returnRequestDeliveredTimeFailTest(){
            // given
            Long orderId = 1L;
            long secondsInOneDay = 24 * 60 * 60;
            long randomSeconds = (long) (Math.random() * secondsInOneDay); // 0 to 86400 seconds
            LocalDateTime randomDateTime = LocalDateTime.now().minusDays(1).minusSeconds(randomSeconds);

            orderedEntity = OrderEntity.builder().orderId(1L).orderStatus(OrderStatus.DELIVERED).orderAddr("지구 어딘가")
                    .orderAddrDetail("한국 어딘가").memberEntity(memberEntity).build();

            // last modify date는 setter없음.. get만 가능
            // 호출 시 값을 임의로 지정해주기 위해서 spy객체로 만들어서 사용
            OrderEntity spyOrderEntity = Mockito.spy(orderedEntity);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(spyOrderEntity));
            when(spyOrderEntity.getLastModifyDate()).thenReturn(randomDateTime);

            // when then
            assertThatThrownBy(
                    ()-> orderService.requestReturn(memberEntity, orderId)
            ).isInstanceOf(AuthorizationException.class).hasMessage(OrderResponseMessage.FAIL_REQUEST_RETURN.getMessage());
        }
    }
}
