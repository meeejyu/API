package com.jwt.domain.jwt;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/*
 * @Redishash(value)란?
 * 어노테이션의 선언된 value로 Redis의 Set 자료 구조를 통해 해당 객체가 저장된다
 * 저장된 객체는 Hash 자료구조로 저장된다. refreshToken : username들이 value로 존재한다
 */
@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
@Builder
public class RefreshToken {
    
    private String id;

    private String refreshToken;

    // 설정한 시간 만큼 데이터를 저장한다. 설정한 시간이 지나면 자동으로 데이터가 사라지는 휘발 역할을 해줍니다.
    // TTL 설정
    @TimeToLive
    private Long expiration;

    public static RefreshToken createRefreshToken(String username, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .id(username)
                .refreshToken(refreshToken)
                .expiration(remainingMilliSeconds / 1000)
                .build();
    }
    
}
