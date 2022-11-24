package com.jwt.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwt.config.jwt.JwtAuthenticationFilter;
import com.jwt.config.jwt.JwtAuthorityHandler;
import com.jwt.config.jwt.JwtEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtEntryPoint jwtEntryPoint; // 시큐리티 필터 과정중 에러가 발생할 경우 처리
    private final JwtAuthorityHandler jwtAuthorityHandler; // 인가에 대한 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // jwt 관련 필터

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/resources/**");
    }

    /*
     * web.ignoring() 은 Spring Security 가 해당 엔드포인트에 보안 헤더 또는 기타 보호 조치를 제공할 수
     * 없음을 의미한다. 따라서 authorizeHttpRequests permitAll 을 사용 할 경우 권한은 검증하지 않으면서
     * 요청을 보호 할수 있으므로 권장된다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/bootstrap/**/**").permitAll()
                .antMatchers("/login", "/main", "/", "/member", "/signup", "/signup/success", "/signup/check",
                        "/member/idCheck").permitAll()
                .antMatchers("/user/**", "/logout", "/reissue").hasRole("USER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated() // 모든 리소스에 대해 인증/인가가 성공이 되어야 접근 할 수 있다
                .and()
                .exceptionHandling() // 예외처리 기능이 작동
                .authenticationEntryPoint(jwtEntryPoint) // 인증실패시 처리
                .accessDeniedHandler(jwtAuthorityHandler) // 인가실패시 처리
                /* jwt 기반으로 로그인/로그아웃을 처리할것이기 때문에 기존 login, logout 배제
                    스프링 시큐리티는 기본 로그인 / 로그아웃 시 세션을 통해 유저 정보들을 저장한다.
                    하지만 Redis를 사용할 것이기 떄문에 상태를 저장하지 않는 STATELESS로 설정
                */ 
                .and()
                .logout().disable()
                // 스프링 시큐리티가 세션을 생성하지도 않고 기존것을 사용하지도 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // jwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter전에 추가
                .build();
    }
}
