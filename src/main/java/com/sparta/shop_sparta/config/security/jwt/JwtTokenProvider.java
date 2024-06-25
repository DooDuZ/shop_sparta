package com.sparta.shop_sparta.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsService userDetailsService;

    @Value("${JWT_SECRET}")
    private String secretKey;
    // 유효기간 15분
    private final long accessTokenValidMillisecond = 1000L * 60 * 15;

    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 7;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String username, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("role", role);

        Date now = new Date();

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }

    public String createRefreshToken(String username, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("role", role);

        Date now = new Date();

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token){
        return Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRole(String token){
        return Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getPayload().get("role").toString();
    }

    public Date getExpireDate(String token){
        return Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public String resolveToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = "";

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("access_token")){
                    token = cookie.getValue();
                }
            }
        }
        //System.out.println(bearerToken);
        if (!token.equals("")) {
            //System.out.println(bearerToken.substring(7));
            return token;
        }
        return null;
    }

    public Boolean isExpired(Jws<Claims> claims){
        return claims.getPayload().getExpiration().before(new Date());
    }

    public Jws<Claims> validateToken(String token) {
        try{
            return Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token);
        }catch (Exception e){
            return null;
        }
    }
}
