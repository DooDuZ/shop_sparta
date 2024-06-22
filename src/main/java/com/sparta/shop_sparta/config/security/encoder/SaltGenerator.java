package com.sparta.shop_sparta.config.security.encoder;

import java.security.SecureRandom;

public class SaltGenerator {

    // 난수 생성 객체
    private static final SecureRandom secureRandom = new SecureRandom();

    // 사용할 솔트의 길이
    // 외부에서 조정 가능하도록 상수를 밖에 선언하고 가져다 써야할까...?
    private static final int saltLength = 4;

    public String generateSalt() {
        // 바이트의 범위
        byte[] saltBytes = new byte[saltLength];
        // 랜덤 바이트 채우기
        secureRandom.nextBytes(saltBytes);

        StringBuilder sb = new StringBuilder();

        // byte를 16진수 변환하면 최대 2자리
        // 균일한 길이를 가지도록 1자리 16진수에도 0을 추가한다
        for (byte b : saltBytes) {
            sb.append(String.format("%02x", b));
        }

        // 8자리 솔트 반환
        return sb.toString();
    }
}
