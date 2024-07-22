package com.sparta.shop_sparta.citest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CiTest {
    @Test
    public void ciTest(){
        int a = 5;
        int b = 5;

        assertThat(a).isEqualTo(b);
    }
}
