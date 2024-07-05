package com.sparta.shop_sparta.service.member.auth;

import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.MemberException;
import com.sparta.shop_sparta.util.encoder.TokenUsernameEncoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsService userDetailsService;

    @Value("${JWT_ACCESS_SECRET}")
    private String accessSecretKey;

    @Value("${JWT_REFRESH_SECRET}")
    private String refreshSecretKey;
    // 유효기간 15분
    private final long accessTokenValidMillisecond = 1000L * 60 * 15;

    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 7;
    private final String prefix = "Bearer ";
    private final String refreshHeaderKey = "JWT_REFRESH_TOKEN";

    private final TokenUsernameEncoder tokenUsernameEncoder;

    @PostConstruct
    protected void init(){
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes(StandardCharsets.UTF_8));
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String username, String role, String memberId){
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("role", role);
        claims.put("member-id", memberId);

        Date now = new Date();

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();

        return token;
    }

    public String createRefreshToken(String username, String role, String memberId, String userAgent){
        Map<String, Object> claims = new HashMap<>();

        //claims.put("sub", UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)).toString());
        try {
            claims.put("sub", tokenUsernameEncoder.encrypt(username));
            claims.put("role", role);
            claims.put("member-id", memberId);
            claims.put("user-agent", userAgent);
        }catch (Exception e){
            throw new MemberException(e);
        }

        Date now = new Date();

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();

        return token;
    }

    public Authentication getAuthentication(String token){
        //UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(getRole(token)));

        MemberEntity memberEntity =  MemberEntity.builder().loginId(getUsername(token))
                .authorities(authorities).memberId(Long.parseLong(getMemberId(token))).build();

        UserDetails userDetails = (UserDetails) memberEntity;

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    // access 전용
    public String getUsername(String token){
        return Jwts.parser().setSigningKey(accessSecretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRole(String token){
        return Jwts.parser().setSigningKey(accessSecretKey).build().parseSignedClaims(token).getPayload().get("role").toString();
    }

    public String getMemberId(String token){
        return Jwts.parser().setSigningKey(accessSecretKey).build().parseSignedClaims(token).getPayload().get("member-id").toString();
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

    public Date getExpireDate(String token){
        return Jwts.parser().setSigningKey(accessSecretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public Jws<Claims> validateToken(String token) {
        try{
            return Jwts.parser().setSigningKey(accessSecretKey).build().parseSignedClaims(token);
        }catch (Exception e){
            return null;
        }
    }
    // access 전용

    // refresh 전용
    public String getUsernameByRefresh(String token){
        return Jwts.parser().setSigningKey(refreshSecretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRoleByRefresh(String token){
        return Jwts.parser().setSigningKey(refreshSecretKey).build().parseSignedClaims(token).getPayload().get("role").toString();
    }

    public String getUserAgentByRefresh(String refreshToken){
        return Jwts.parser().setSigningKey(refreshSecretKey).build().parseSignedClaims(refreshToken).getPayload().get("user-agent").toString();
    }

    public String getRefreshMemberId(String token){
        return Jwts.parser().setSigningKey(refreshSecretKey).build().parseSignedClaims(token).getPayload().get("member-id").toString();
    }

    public String resolveRefreshToken(HttpServletRequest request){
        String bearerToken = request.getHeader(refreshHeaderKey);

        if (bearerToken != null && bearerToken.startsWith(prefix)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public Jws<Claims> validateRefreshToken(String token) {
        try {
            return Jwts.parser().setSigningKey(refreshSecretKey).build().parseSignedClaims(token);
        }catch (SignatureException se){
            se.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    // refresh 전용


    public Boolean isExpired(Jws<Claims> claims){
        return claims.getPayload().getExpiration().before(new Date());
    }
}
