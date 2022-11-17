package com.jwt.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwt.config.jwt.JwtAuthenticationFilter;
import com.jwt.config.jwt.JwtEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtEntryPoint jwtEntryPoint; // 시큐리티 필터 과정중 에러가 발생할 경우 처리
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // jwt 관련 필터
    private final CustomUserDetailService customUserDetailService; // userDetailsService라는 유저의 정보를 가져오기 위한 클래스

    // 비밀번호 암호화
    // 비밀번호 검사 하는 로직
    /*
     * 사용할 userDetailsService를 passwordEncoder에 명시적으로 설정했었는데
     * Bean으로 들옭만 해두면 스프링 시큐리티에 의해 해당 빈들이 사용된다.
     */
    // 이전 코드
    // @Override
    // protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     auth.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
    // }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                        "/member/idCheck", "/member/nickCheck", "/login/check","/favicon.ico", "/login/success").permitAll()
                .antMatchers("/user/**", "/logout", "/reissue").hasRole("USER")
                .anyRequest().authenticated() // 인증이 되어야한다
                
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint)

                /* jwt 기반으로 로그인/로그아웃을 처리할것이기 때문에 기존 login, logout 배제
                    스프링 시큐리티는 기본 로그인 / 로그아웃 시 세션을 통해 유저 정보들을 저장한다.
                    하지만 Redis를 사용할 것이기 떄문에 상태를 저장하지 않는 STATELESS로 설정
                */ 
                

                // .and()
                // .formLogin()
                // .loginPage("/login").permitAll()
                // .defaultSuccessUrl("/index")
                
                .and()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // .logoutUrl("/logout")

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // jwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter전에 추가
                .build();
    }

    // protected void configure(Aute)

    /*
     * resources(css, js 등) 의 경우 securityContext 등에 대한 조회가 불필요 하므로 disable 한다
     * @Order(0) 을 추가하여 먼저 FilterChain 을 타도록 지정한다
     */
    // @Bean
    // @Order(0)
    // public SecurityFilterChain resources(HttpSecurity http) throws Exception {
    //     return http.requestMatchers(matchers -> matchers
    //             .antMatchers("/resources/**"))
    //             .authorizeHttpRequests(authorize -> authorize
    //                     .anyRequest().permitAll())
    //             .requestCache(RequestCacheConfigurer::disable)
    //             .securityContext(AbstractHttpConfigurer::disable)
    //             .sessionManagement(AbstractHttpConfigurer::disable)
    //             .build();
    // }

}
