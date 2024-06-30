package com.sparta.shop_sparta.constant.order;

public enum OrderResponseMessage {
    SUCCESS_ORDER("[주문 성공]"),
    COMPLETE_ORDER("[주문 처리 완료] - 이미 처리된 주문입니다."),
    FAIL_PAYMENT("[결제 실패] 결제 수단을 확인해주세요."),
    OUT_OF_STOCK("[재고 부족] 남은 수량이 적습니다."),
    INVALID_ORDER_STATUS("주문 정보 상태 변경 오류"),
    INVALID_ORDER("[주문 조회 실패] 유효하지 않은 주문 번호입니다."),
    ;

    private final String message;

    OrderResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
