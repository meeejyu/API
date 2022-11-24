package com.jwt.main.service;

import java.util.Map;

import com.jwt.domain.LoginDto;

public interface MainService {
    
    Map<String, Object> getAuthority(String id);

    Map<String, Object> memberLoginId(String loginId);

    Map<String, Object> memberUsername(String username);   

    Map<String, Object> getLoginMember(LoginDto loginDto);
    
}
