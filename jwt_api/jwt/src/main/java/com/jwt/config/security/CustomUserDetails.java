package com.jwt.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/*
 * 인증과 권한에 필요한 정보들을 필드에 설정하고, Redis에 저장할 때 관련이 없는 나머지 메서드들을 @JsonIgnoe로 처리
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails{
    
    private String username;
    private String password;

    private List<String> roles = new ArrayList<>();
    
    public static UserDetails of(Map<String, Object> memberMap) {
        return CustomUserDetails.builder()
                .username(memberMap.get("MEMBER_USERNAME").toString())
                .password(memberMap.get("MEMBER_PW").toString())
                .roles((List<String>)memberMap.get("AUTHORITY_ROLE"))
                .build();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return false;
    }
}
