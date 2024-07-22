package com.sparta.common.constant.product;

import org.springframework.http.HttpStatus;

public enum ProductMessage {
    FAIL_IO_IMAGE("[서버 오류] 파일 입출력 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND_THUMBNAIL("썸네일 없음", HttpStatus.NOT_FOUND),
    TOO_MANY_IMAGES("이미지 개수 초과[ 썸네일 : 최대 5장 / 본문 : 최대 5장 ]", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("파일 크기가 허용된 최대 크기를 초과했습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PRODUCT("존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
    INVALID_CATEGORY("카테고리 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_STATUS("[판매 상태 변경 오류] 상태 번호를 확인해주세요.", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK("재고 부족", HttpStatus.BAD_REQUEST),
    FAIL_TO_CONNECT("요청 과부하 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_ON_SALE("판매 중인 상품이 아닙니다.", HttpStatus.BAD_REQUEST),
    INVALID_RESERVATION("예약 정보가 없습니다.", HttpStatus.BAD_REQUEST),
    FAIL_S3_UPLOAD("S3 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    private ProductMessage(String message, HttpStatus httpStatus) {
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
