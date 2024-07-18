package com.sparta.shop_sparta.constant.order;

import org.springframework.http.HttpStatus;

public enum OrderResponseMessage {
    SUCCESS_ORDER("[주문 성공]", HttpStatus.OK),
    //COMPLETE_ORDER("[주문 처리 완료] - 이미 처리된 주문입니다.", ),
    FAIL_PAYMENT("[결제 실패] 결제 수단을 확인해주세요.", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK("[재고 부족] 남은 수량이 적습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS("[주문 정보 상태 변경 오류] 상태 넘버를 확인해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER("[주문 조회 실패] 유효하지 않은 주문 번호입니다.", HttpStatus.BAD_REQUEST),
    FAIL_CANCEL("[배송중] 주문 취소 불가. 배송 시작 전에만 취소 가능합니다.", HttpStatus.BAD_REQUEST),
    FAIL_REQUEST_RETURN("[반품 기한 만료] 반품 신청 불가", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST("요청 정보와 장바구니 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT("1개 이상 주문 가능", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    OrderResponseMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
