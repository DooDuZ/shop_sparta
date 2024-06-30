package com.sparta.shop_sparta.constant.order;

import com.sparta.shop_sparta.exception.OrderException;

public enum OrderStatus {
    PREPARED(0),           // 상품 준비중, 배송 시작 전
    CANCELLED(1),          // 주문 취소
    IN_DELIVERY(2),        // 배송중
    DELIVERED(3),          // 배송 완료
    RETURN_REQUESTED(4),   // 반품 요청 접수
    IN_RETURN(5),          // 반품 진행중
    RETURN_COMPLETED(6),    // 반품 완료
    ;

    private final long status;

    OrderStatus(long status) {
        this.status = status;
    }

    public long getStatus() {
        return this.status;
    }

    public static OrderStatus of(long status){
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getStatus() == status) {
                return orderStatus;
            }
        }

        throw new OrderException(OrderResponseMessage.INVALID_ORDER_STATUS.getMessage());
    }
}
