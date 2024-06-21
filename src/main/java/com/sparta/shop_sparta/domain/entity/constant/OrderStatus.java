package com.sparta.shop_sparta.domain.entity.constant;

public enum OrderStatus {
    PREPARED,           // 상품 준비중, 배송 시작 전
    CANCELLED,          // 주문 취소
    IN_DELIVERY,        // 배송중
    DELIVERED,          // 배송 완료
    RETURN_REQUESTED,   // 반품 요청 접수
    IN_RETURN,          // 반품 진행중
    RETURN_COMPLETED    // 반품 완료
}
