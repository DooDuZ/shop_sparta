package com.sparta.shop_sparta.service.order;

import com.sparta.shop_sparta.constant.order.OrderResponseMessage;
import com.sparta.shop_sparta.constant.order.OrderStatus;
import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderResponseDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.exception.OrderException;
import com.sparta.shop_sparta.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto) {
        // 결제가 성공했다면
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = orderRequestDto.toEntity();
        orderEntity.setMemberEntity(memberEntity);
        // 첫 주문은 항상 배송 준비 상태
        orderEntity.setOrderStatus(OrderStatus.PREPARED);
        // 저장하고
        orderRepository.save(orderEntity);

        // orderDetail 정보 처리
        Long totalPrice = orderDetailService.addOrder(orderEntity, orderRequestDto.getOrderDetails());
        // 가격 입력
        orderEntity.setTotalPrice(totalPrice);

        // 재고 확인까지 끝난 상태로 결제한다.
        if (!paymentService.pay(orderEntity)){
            // details 또한 실패한 주문을 참조하고 있기 때문에 삭제할 필요 없다
            // 되려 실패한 주문 조회하려고 하면 꺼내서 보여줘야함
            // 일반적으로 실패한 주문을 다시 조회하려고 하진 않을듯하니 일단 soft delete 상태로 두자
            orderEntity.setDelete(true);
            throw new OrderException(OrderResponseMessage.FAIL_PAYMENT.getMessage());
        }

        // 결제된 주문 정보 반환을 위해 생성
        OrderResponseDto orderResponseDto = orderEntity.toDto();
        orderResponseDto.setOrderDetails(orderRequestDto.getOrderDetails());

        return ResponseEntity.ok(orderResponseDto);
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
        return null;
    }
}
