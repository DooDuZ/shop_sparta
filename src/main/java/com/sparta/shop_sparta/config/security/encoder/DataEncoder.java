package com.sparta.shop_sparta.config.security.encoder;

public interface DataEncoder {
    String encrypt(String data, String salt);
}
