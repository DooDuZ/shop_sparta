package com.sparta.shop_sparta.service.order;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.order.OrderResponseMessage;
import com.sparta.common.constant.order.OrderStatus;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.common.exception.OrderException;
import com.sparta.shop_sparta.domain.dto.cart.CartRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderDetailRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderRequestDto;
import com.sparta.shop_sparta.domain.dto.order.OrderResponseDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderDetailEntity;
import com.sparta.shop_sparta.domain.entity.order.OrderEntity;
import com.sparta.shop_sparta.repository.OrderRepository;
import com.sparta.shop_sparta.service.cart.CartService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final PaymentService paymentService;
    private final CartService cartService;

    @Transactional
    public void createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = orderRequestDto.toEntity();
        orderEntity.setMemberEntity(memberEntity);
        // 첫 주문은 항상 배송 준비 상태
        orderEntity.setOrderStatus(OrderStatus.PREPARED);
        // 저장하고
        orderRepository.save(orderEntity);

        orderRequestDto.setOrderId(orderEntity.getOrderId());

        // 장바구니에 있는 상품만 결제 하도록 처리 -> 정책임!!
        List<OrderDetailRequestDto> requestOrderDetails = getOrderDetailsInCart(orderRequestDto, memberEntity);

        // orderDetail 정보 처리
        List<OrderDetailEntity> orderDetailEntities = orderDetailService.addOrder(orderEntity, requestOrderDetails);

        // 비동기 저장 처리 - 결제 실패시 Exception 발생
        orderDetailService.orderDetailSaveAll(orderDetailEntities);

        // 가격 입력
        orderEntity.setTotalPrice(getTotalPrice(orderDetailEntities));

        // 재고 확인까지 끝난 상태로 결제한다.
        // 결제 실패 시 재고 복구
        if (!paymentService.pay(orderEntity)) {
            orderDetailService.rollbackOrder(orderDetailEntities);
            throw new OrderException(OrderResponseMessage.FAIL_PAYMENT);
        }

        // 결제된 주문 정보 반환을 위해 생성
        /*OrderResponseDto orderResponseDto = orderEntity.toDto();
        orderResponseDto.setOrderDetails(orderDetailDtoList);*/

        // 결제 성공한 데이터는 장바구니에서 제거
        // Todo - 원상 복구 대상
        // cartService.removeOrderedProduct(memberEntity, orderDetailDtoList);
    }

    private Long getTotalPrice(List<OrderDetailEntity> orderDetails) {
        Long totalPrice = 0L;

        for (OrderDetailEntity orderDetail : orderDetails) {
            totalPrice += orderDetail.getAmount() * orderDetail.getProductEntity().getPrice();
        }

        return totalPrice;
    }

    private List<OrderDetailRequestDto> getOrderDetailsInCart(OrderRequestDto orderRequestDto,
                                                              MemberEntity memberEntity) {
        List<OrderDetailRequestDto> orderDetails = new ArrayList<>();

        // 장바구니 정보 가져오기
        Map<Long, Long> cartInfo = cartService.getCartInfo(memberEntity);

        for (OrderDetailRequestDto orderDetailDto : orderRequestDto.getOrderDetails()) {
            // Todo -> 여기서부터 고쳐
            Long productId = orderDetailDto.getProductId();

            // 장바구니에 상품 정보가 없거나, 요청 수량이 다르면
            // Todo 복구 대상
            /*
            if (!cartInfo.containsKey(productId) || cartInfo.get(productId) - orderDetailDto.getAmount() != 0) {
                throw new OrderException(OrderResponseMessage.INVALID_REQUEST.getMessage());
            }
             */

            if (!cartInfo.containsKey(productId) || cartInfo.get(productId) - orderDetailDto.getAmount() < 0) {
                throw new OrderException(OrderResponseMessage.INVALID_REQUEST);
            }

            // Todo 테스트용 코드 - 삭제 대상
            // 수량 완전 일치 시에만 주문 가능. 주문 완료 후 장바구니에서 상품 삭제 예정

            Long amount = cartInfo.get(productId) - orderDetailDto.getAmount();
            cartService.updateCartDetail(memberEntity, new CartRequestDto(amount, productId));

            orderDetails.add(orderDetailDto);
        }

        return orderDetails;
    }

    public OrderResponseDto getOrder(UserDetails userDetails, Long orderId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = getValidEntity(memberEntity, orderId);

        OrderResponseDto orderResponseDto = orderEntity.toDto();
        orderResponseDto.setOrderDetails(orderDetailService.getOrderedProduct(orderEntity));

        return orderResponseDto;
    }

    @Transactional
    public OrderResponseDto cancelOrder(UserDetails userDetails, Long orderId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = getValidEntity(memberEntity, orderId);

        if (orderEntity.getOrderStatus() != OrderStatus.PREPARED) {
            throw new OrderException(OrderResponseMessage.FAIL_CANCEL);
        }

        // [Todo] 환불 로직 추가

        // 재고 반환
        orderDetailService.cancelOrder(orderEntity);

        orderEntity.setOrderStatus(OrderStatus.CANCELLED);

        return orderEntity.toDto();
    }

    @Transactional
    public OrderResponseDto requestReturn(UserDetails userDetails, Long orderId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = getValidEntity(memberEntity, orderId);

        if (orderEntity.getOrderStatus() != OrderStatus.DELIVERED
                || ChronoUnit.DAYS.between(orderEntity.getLastModifyDate(),
                LocalDateTime.now()) >= 1) {
            throw new OrderException(OrderResponseMessage.FAIL_REQUEST_RETURN);
        }

        orderEntity.setOrderStatus(OrderStatus.RETURN_REQUESTED);

        return orderEntity.toDto();
    }

    public List<OrderResponseDto> getOrders(UserDetails userDetails, int page, int itemsPerPage) {
        PageRequest pageable = PageRequest.of(page - 1, itemsPerPage);

        List<OrderEntity> orderEntities = orderRepository.findAllByMemberEntity(pageable, (MemberEntity) userDetails)
                .getContent();

        List<OrderDetailEntity> orderDetailEntities = orderDetailService.getOrderDetailsByOrderEntities(orderEntities);

        Map<Long, OrderResponseDto> orderInfo = orderEntities.stream().map(OrderEntity::toDto).collect(
                Collectors.toMap(OrderResponseDto::getOrderId, Function.identity())
        );

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            orderInfo.get(orderDetailEntity.getOrderEntity().getOrderId()).getOrderDetails().add(orderDetailEntity.toDto());
        }

        return orderInfo.values().stream()
                .sorted(Comparator.comparing(OrderResponseDto::getOrderId))
                .collect(Collectors.toList());
    }

    private OrderEntity getValidEntity(MemberEntity memberEntity, Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderResponseMessage.INVALID_ORDER)
        );

        if (memberEntity.getMemberId() - orderEntity.getMemberEntity().getMemberId() != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        return orderEntity;
    }

    //@Scheduled(fixedDelay = 60000)
    // 테스트 - 5분마다 상태 변경
    // Todo 배치 서버로 옮길 것
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
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

            if (nextStatus == OrderStatus.RETURN_COMPLETED) {
                orderDetailService.cancelOrder(orderEntity);

                // Todo 환불 로직 추가
                // 환불은 이 트랜잭션 끝나고 몰아서 처리해야할듯
            }
        }
    }
}
