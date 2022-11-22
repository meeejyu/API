package com.jwt.comm.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.jwt.config.jwt.JwtExpirationEnums;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenUtil {
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    /*
     * 토큰 추출 메서드
     * 서명했을 때의 secretkey로 서명하고 토큰을 만들때 username, 발급날짜, 만료기간을 넣었던 payload를 가져옴
     */
    public Claims extractAllClaims(String token) { 
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 만료 확인
    public Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String generateAccessToken(String username) {
        // return doGenerateToken(username, ACCESS_TOKEN_EXPIRATION_TIME.getValue());
        return doGenerateToken(username, JwtExpirationEnums.ACCESS_TOKEN_EXPIRATION_TIME.getValue());
    }

    public String generateRefreshToken(String username) {
        // return doGenerateToken(username, REFRESH_TOKEN_EXPIRATION_TIME.getValue());
        return doGenerateToken(username, JwtExpirationEnums.REFRESH_TOKEN_EXPIRATION_TIME.getValue());
    }

    /*
     * 토큰 생성 메소드
     * JWT는 header.payload.signature로 구성되어 있다.
     * username, 발급날짜, 만료기간을 payload에 넣고 앞에 yml에서 설정한 secrekey로 서명 후 HS256 알고리즘으로 암호화합니다
     */
    private String doGenerateToken(String username, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token) {
        // String username = getUsername(token); // 중복된 로직
        // return username.equals(userDetails.getUsername())
        //         && !isTokenExpired(token);
        return !isTokenExpired(token);
    }

    public long getRemainMilliSeconds(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }
}