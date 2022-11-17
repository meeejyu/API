package com.jwt.config.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwt.config.cache.CacheKey;
import com.jwt.main.service.MainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MainService mainService;
    
    @Override
    @Cacheable(value = CacheKey.USER, key = "#username", unless = "#result == null")
    // 토큰을 줄때 마다 데이터베이스를 거치는 것을 줄이기 위해 설정
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Map<String, Object> memberMap = mainService.memberUsername(username);

        Map<String, Object> map = memberMap;

        Map<String, Object> authorityMap = mainService.getAuthority(memberMap.get("MEMBER_ID").toString());

        List<String> role = new ArrayList<>();
        role.add(authorityMap.get("AUTHORITY_ROLE").toString());
        map.put("AUTHORITY_ROLE", role);

        return CustomUserDetails.of(map);
    }
    
}
