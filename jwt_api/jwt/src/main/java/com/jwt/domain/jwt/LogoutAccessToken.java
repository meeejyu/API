package com.jwt.domain.jwt;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash("logoutAccessToken")
@AllArgsConstructor
@Builder
public class LogoutAccessToken {

    private String id;

    private String username;

    @TimeToLive
    private Long expiration;

    public static LogoutAccessToken of(String accessToken, String username, Long remainingMilliSeconds) {
        return LogoutAccessToken.builder()
                .id(accessToken)
                .username(username)
                .expiration(remainingMilliSeconds / 1000)
                .build();
    }
}