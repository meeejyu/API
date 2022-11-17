package com.jwt.main.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jwt.domain.Authority;
import com.jwt.domain.LoginDto;
import com.jwt.domain.Member;

@Mapper
public interface MainMapper {
    
    List<Map<String, Object>> getMemberList();

    void memberSave(Member member);
    
    void authoritySave(Authority authority);

    Map<String, Object> getAuthority(String id);

    Map<String, Object> memberLoginId(String loginId);

    Map<String, Object> memberNickname(String nickname);

    Map<String, Object> memberUsername(String username);    

    Map<String, Object> getLoginMember(LoginDto loginDto);

}

