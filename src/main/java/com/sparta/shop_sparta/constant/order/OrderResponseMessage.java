package com.sparta.shop_sparta.constant.order;

public enum OrderResponseMessage {
    SUCCESS_ORDER("[주문 성공]"),
    COMPLETE_ORDER("[주문 처리 완료] - 이미 처리된 주문입니다."),
    FAIL_PAYMENT("[결제 실패] 결제 수단을 확인해주세요."),
    SOLD_OUT_ITEM("[재고 소진] 남은 수량이 적습니다."),
    INVALID_ORDER_STATUS("주문 정보 상태 변경 오류")
    ;

    private final String message;

    OrderResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
