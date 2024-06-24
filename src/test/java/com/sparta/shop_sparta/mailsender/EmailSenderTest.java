package com.sparta.shop_sparta.mailsender;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailSenderTest {

    @Value("${secrets.REQUEST_URL}")
    String url;
    @Test
    void test(){
        System.out.println(url);
    }
}
