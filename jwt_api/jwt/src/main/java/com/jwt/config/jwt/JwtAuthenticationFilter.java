package com.jwt.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jwt.comm.util.JwtTokenUtil;
import com.jwt.config.security.CustomUserDetailService;
import com.jwt.domain.jwt.LogoutAccessTokenRedisRepository;
import com.jwt.domain.jwt.RefreshTokenRedisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailService customUserDetailService;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // getToken 메서드로 헤더에서 JWT를 Bearer를 제외하여 가져온다. 만약 null이면 그대로 반환한다
        String accessToken = getToken(request); 
        if(accessToken !=null) {

            // 로그아웃 검증
            checkLogout(accessToken);
            
            String username = jwtTokenUtil.getUsername(accessToken);

            if(username !=null) {

                // 만료된 토큰인지 확인
                refreshTokenRedisRepository.findById(username).orElseThrow(() -> new IllegalArgumentException("토큰이 일치하지 않습니다."));

                UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

                // UserDetails에서 가져온 username과 토큰에서 가져온 username 비교
                validateAccessToken(accessToken);

                // securityContext에 해당 user정보를 넣어줌
                processSecurity(request, userDetails);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private void checkLogout(String accessToken) {
        if(logoutAccessTokenRedisRepository.existsById(accessToken)) {
            throw new IllegalArgumentException("이미 로그아웃된 회원입니다.");
        }
    }

    private void validateAccessToken(String accessToken) {
        if(!jwtTokenUtil.validateToken(accessToken)) {
            throw new IllegalArgumentException("토큰 검증 실패");
        }
    }

    // 유저정보 SecurityContext에 저장
    private void processSecurity(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
    
}