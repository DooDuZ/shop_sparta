package com.sparta.shop_sparta.constant.order;

public enum OrderResponseMessage {
    SUCCESS_ORDER("[주문 성공]"),
    COMPLETE_ORDER("[주문 처리 완료] - 이미 처리된 주문입니다."),
    FAIL_PAYMENT("[결제 실패] 결제 수단을 확인해주세요."),
    OUT_OF_STOCK("[재고 부족] 남은 수량이 적습니다."),
    INVALID_ORDER_STATUS("주문 정보 상태 변경 오류"),
    INVALID_ORDER("[주문 조회 실패] 유효하지 않은 주문 번호입니다."),
    FAIL_CANCEL("[배송중] 주문 취소 불가. 배송 시작 전에만 취소 가능합니다."),
    FAIL_REQUEST_RETURN("[반품 기한 만료] 반품 신청 불가"),
    INVALID_REQUEST("요청 정보와 장바구니 정보가 일치하지 않습니다."),
    INVALID_AMOUNT("1개 이상 주문 가능"),
    ;

    private final String message;

    OrderResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
