package com.sparta.shop_sparta.order.service;

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
import com.sparta.shop_sparta.order.domain.entity.OrderDetailEntity;
import com.sparta.shop_sparta.order.domain.entity.OrderEntity;
import com.sparta.shop_sparta.order.repository.OrderRepository;
import com.sparta.shop_sparta.cart.service.CartService;
import com.sparta.shop_sparta.product.service.ProductService;
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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final PaymentService paymentService;
    private final CartService cartService;
    private final ProductService productService;

    @Transactional
    public void createOrder(UserDetails userDetails, OrderRequestDto orderRequestDto) {
        MemberEntity memberEntity = (MemberEntity) userDetails;
        OrderEntity orderEntity = saveAndInitOrderEntity(memberEntity, orderRequestDto);

        orderRequestDto.setOrderId(orderEntity.getOrderId());

        // 주문 처리
        List<OrderDetailRequestDto> requestOrderDetails = getOrderDetailsInCart(orderRequestDto, memberEntity);
        List<OrderDetailEntity> orderDetailEntities = orderDetailService.addOrder(orderEntity, requestOrderDetails);

        // 가격 입력
        orderEntity.setTotalPrice(getTotalPrice(orderDetailEntities));

        // 재고 확인까지 끝난 상태로 결제한다.
        // 결제 실패 시 재고 복구
        if (!paymentService.pay(orderEntity)) {
            orderDetailService.rollbackOrder(orderDetailEntities);
            throw new OrderException(OrderResponseMessage.FAIL_PAYMENT);
        }

        // 비동기 저장 처리
        orderDetailService.orderDetailSaveAll(orderDetailEntities);

        // 결제 성공한 데이터는 장바구니에서 제거
        cartService.removeOrderedProduct(memberEntity, requestOrderDetails);

        // 결제된 주문 정보 반환을 위해 생성
        /*OrderResponseDto orderResponseDto = orderEntity.toDto();
        orderResponseDto.setOrderDetails(orderDetailDtoList);*/
    }

    private OrderEntity saveAndInitOrderEntity(MemberEntity memberEntity, OrderRequestDto orderRequestDto){
        OrderEntity orderEntity = orderRequestDto.toEntity();

        log.info(memberEntity.toString());

        // 첫 주문은 항상 배송 준비 상태
        orderEntity.setMemberEntity(memberEntity);
        orderEntity.setOrderStatus(OrderStatus.PREPARED);
        // 저장하고
        return orderRepository.save(orderEntity);
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
            if (!cartInfo.containsKey(productId) || cartInfo.get(productId) - orderDetailDto.getAmount() != 0) {
                try {
                    throw new OrderException(OrderResponseMessage.INVALID_REQUEST);
                }catch (Exception e){
                    log.info("" + cartInfo.get(productId) + " - " +  orderDetailDto.getAmount());
                }
            }

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
            OrderDetailDto orderDetailDto = orderDetailEntity.toDto();
            orderDetailDto.setProductDto(productService.getProductDto(orderDetailEntity.getProductEntity()));
            orderInfo.get(orderDetailEntity.getOrderEntity().getOrderId()).getOrderDetails().add(orderDetailDto);
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
