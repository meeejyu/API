package com.jwt.main.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jwt.domain.LoginDto;

@Mapper
public interface MainMapper {
    
    Map<String, Object> getAuthority(String id);

    Map<String, Object> memberLoginId(String loginId);

    Map<String, Object> memberUsername(String username);    

    Map<String, Object> getLoginMember(LoginDto loginDto);

}

