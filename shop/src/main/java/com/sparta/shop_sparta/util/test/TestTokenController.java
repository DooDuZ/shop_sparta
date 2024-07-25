package com.sparta.shop_sparta.util.test;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token-test")
@RequiredArgsConstructor
public class TestTokenController {
    private final TestTokenService testTokenService;

    @PostMapping("/token")
    public ResponseEntity<Void> getToken(@RequestBody UserInfo userInfo, HttpServletResponse httpServletResponse){
        testTokenService.getToken(userInfo, httpServletResponse);
        return ResponseEntity.ok().build();
    }
}
