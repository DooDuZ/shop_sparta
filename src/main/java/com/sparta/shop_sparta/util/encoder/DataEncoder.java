package com.sparta.shop_sparta.util.encoder;

public interface DataEncoder {
    String encrypt(String data, String salt);
}
