package com.sparta.shop_sparta.config.encoder;

public interface DataEncoder {
    String encrypt(String data, String salt);
}
