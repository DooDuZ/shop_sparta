package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.order.OrderResponseMessage;
import com.sparta.shop_sparta.constant.order.OrderStatus;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailDto;
import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderResponseDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.OrderException;
import com.sparta.shop_sparta.repository.OrderRepository;
import com.sparta.shop_sparta.service.cart.CartService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final PaymentService paymentService;
    private final CartService cartService;

    @Override
    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = orderRequestDto.toEntity();
        orderEntity.setMemberEntity(memberEntity);
        // 첫 주문은 항상 배송 준비 상태
        orderEntity.setOrderStatus(OrderStatus.PREPARED);
        // 저장하고
        orderRepository.save(orderEntity);

        // 장바구니에 있는 상품만 결제 하도록 처리 -> 정책임!!
        List<OrderDetailDto> orderDetails = getOrderDetailsInCart(orderRequestDto, memberEntity);

        // orderDetail 정보 처리
        Long totalPrice = orderDetailService.addOrder(orderEntity, orderDetails);
        // 가격 입력
        orderEntity.setTotalPrice(totalPrice);

        // 재고 확인까지 끝난 상태로 결제한다.
        if (!paymentService.pay(orderEntity)){
            throw new OrderException(OrderResponseMessage.FAIL_PAYMENT.getMessage());
        }

        // 결제된 주문 정보 반환을 위해 생성
        OrderResponseDto orderResponseDto = orderEntity.toDto();
        orderResponseDto.setOrderDetails(orderDetails);

        // 결제 성공한 데이터는 장바구니에서 제거
        cartService.removeOrderedProduct(memberEntity, orderDetails);

        return ResponseEntity.ok(orderResponseDto);
    }

    private List<OrderDetailDto> getOrderDetailsInCart(OrderRequestDto orderRequestDto, MemberEntity memberEntity){
        List<OrderDetailDto> orderDetails = new ArrayList<>();

        // 장바구니 정보 가져오기
        Map<Long, Long> cartInfo = cartService.getCartInRedis(memberEntity);

        for (OrderDetailDto orderDetailDto : orderRequestDto.getOrderDetails()) {
            Long productId = orderDetailDto.getProductId();
            // 장바구니에 상품 정보가 없거나, 요청 수량이 다르면
            if (!cartInfo.containsKey(productId) || cartInfo.get(productId) - orderDetailDto.getAmount() != 0) {
                throw new OrderException(OrderResponseMessage.INVALID_REQUEST.getMessage());
            }

            orderDetails.add(orderDetailDto);
        }

        return orderDetails;
    }

    @Override
    public ResponseEntity<OrderResponseDto> getOrders(UserDetails userDetails, Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderResponseMessage.INVALID_ORDER.getMessage())
        );

        OrderResponseDto orderResponseDto = orderEntity.toDto();
        orderResponseDto.setOrderDetails(orderDetailService.getOrderedProduct(orderEntity));

        return ResponseEntity.ok(orderResponseDto);
    }

    @Override
    @Transactional
    public ResponseEntity<?> cancelOrder(UserDetails userDetails, Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderResponseMessage.INVALID_ORDER.getMessage())
        );

        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (memberEntity.getMemberId() - orderEntity.getMemberEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        if(orderEntity.getOrderStatus() != OrderStatus.PREPARED){
            throw new OrderException(OrderResponseMessage.FAIL_CANCEL.getMessage());
        }

        // [Todo] 환불 로직 추가

        // 재고 반환
        orderDetailService.cancelOrder(orderEntity);

        orderEntity.setOrderStatus(OrderStatus.CANCELLED);

        return ResponseEntity.ok().body(orderEntity.toDto());
    }

    @Override
    @Transactional
    public ResponseEntity<?> requestReturn(UserDetails userDetails, Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderResponseMessage.INVALID_ORDER.getMessage())
        );

        MemberEntity memberEntity = (MemberEntity) userDetails;

        if(memberEntity.getMemberId() - orderEntity.getMemberEntity().getMemberId() != 0){
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED.getMessage());
        }

        if (orderEntity.getOrderStatus() != OrderStatus.DELIVERED || ChronoUnit.DAYS.between(orderEntity.getLastModifyDate(),
                LocalDateTime.now()) > 1 ) {
            throw new OrderException(OrderResponseMessage.FAIL_REQUEST_RETURN.getMessage());
        }

        orderEntity.setOrderStatus(OrderStatus.RETURN_REQUESTED);

        return ResponseEntity.ok().build();
    }

    //@Scheduled(fixedDelay = 60000)
    // 테스트 - 5분마다 상태 변경
    @Transactional
    @Scheduled(cron = "0 27 3 * * ?")
    protected void updateOrderStatus() {
        updateOrderStatus(OrderStatus.IN_DELIVERY, OrderStatus.DELIVERED);
        updateOrderStatus(OrderStatus.PREPARED, OrderStatus.IN_DELIVERY);
        updateOrderStatus(OrderStatus.IN_RETURN, OrderStatus.RETURN_COMPLETED);
        updateOrderStatus(OrderStatus.RETURN_REQUESTED, OrderStatus.IN_RETURN);
    }

    private void updateOrderStatus(OrderStatus prevStatus, OrderStatus nextStatus) {
        List<OrderEntity> orderEntities = orderRepository.findByOrderStatus(prevStatus);
        for (OrderEntity orderEntity : orderEntities) {
            orderEntity.setOrderStatus(nextStatus);

            if (nextStatus == OrderStatus.RETURN_COMPLETED){
                orderDetailService.cancelOrder(orderEntity);

                // Todo 환불 로직 추가
                // 환불은 이 트랜잭션 끝나고 몰아서 처리해야할듯
            }
        }
    }
}
