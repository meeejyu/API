package com.jwt.main.service;

import java.util.List;
import java.util.Map;

import com.jwt.domain.Authority;
import com.jwt.domain.LoginDto;
import com.jwt.domain.MemberInsertDto;

public interface MainService {
    
    List<Map<String, Object>> getMemberList();

    void memberSave(MemberInsertDto memberInsertDto);
    
    void authoritySave(Authority authority);

    Map<String, Object> getAuthority(String id);

    Map<String, Object> memberLoginId(String loginId);

    Map<String, Object> memberNickname(String nickname);

    Map<String, Object> memberUsername(String username);   

    Map<String, Object> getLoginMember(LoginDto loginDto);
    
}
