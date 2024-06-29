package com.sparta.shop_sparta.constant.product;

public enum ProductMessage {
    FAIL_STORE_IMAGE("[서버 오류] 파일 저장 실패"),
    NOT_FOUND_THUMBNAIL("썸네일 없음"),
    TOO_MANY_IMAGES("이미지 개수 초과[ 썸네일 : 최대 5장 / 본문 : 최대 5장 ]"),
    FILE_SIZE_EXCEEDED("파일 크기가 허용된 최대 크기를 초과했습니다."),
    NOT_FOUND_PRODUCT("존재하지 않는 상품입니다."),
    INVALID_CATEGORY("카테고리 정보가 존재하지 않습니다.")
    ;

    private String message;

    private ProductMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
