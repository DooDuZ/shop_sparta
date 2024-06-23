package com.sparta.shop_sparta.util.encoder;

public interface EnableDecoding extends DataEncoder{
    String decrypt(String encodeData, String salt);
}
