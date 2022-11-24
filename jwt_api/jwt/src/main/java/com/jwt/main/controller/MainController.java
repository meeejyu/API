package com.jwt.main.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jwt.comm.util.JwtTokenUtil;
import com.jwt.domain.LoginDto;
import com.jwt.domain.TokenDto;
import com.jwt.main.service.MainService;
import com.jwt.main.serviceImpl.MainServiceImpl;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    private final MainServiceImpl mainServiceImpl;

    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.ok(mainServiceImpl.reissue(refreshToken));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> loginSuccess(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {

        loginDto.encryptionPass(loginDto);

        System.out.println("추카 포카");

        return ResponseEntity.ok(mainServiceImpl.getLoginMemberToken(loginDto));
    }

    // USER 권한 있을시 접근 가능
    @GetMapping("/user/main")
    public @ResponseBody String userMain() {
        System.out.println("추카 포카");

        return "userMain";
    }

    @GetMapping("/admin/main")
    public @ResponseBody String adminMain() {
        System.out.println("추카 포카");

        return "adminMain";
    }

    // 스프링 시큐리티에 명시 안된 코드
    @GetMapping("/testAuth")
    public @ResponseBody String testAuth(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("추카 포카");

        return "testAuth";
    }

    @PostMapping("/member/idCheck")
    public @ResponseBody Map<String,Object> memberidCheck(String LOGIN_ID) {
        
        System.out.println("값이 잘오나 확인"+LOGIN_ID);
        Map<String,Object> memberMap = mainService.memberLoginId(LOGIN_ID);

        if(memberMap==null) {
            memberMap = new HashMap<>();
            memberMap.put("fail", "없는 이름입니다.");
            return memberMap;            
        }
        else {
            return memberMap;
        }
    }

    @PostMapping("/logout")
    public @ResponseBody String logout(@RequestHeader("Authorization") String accessToken, 
                        @RequestHeader("RefreshToken") String refreshToken) {
        
        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
        mainServiceImpl.logout(TokenDto.of(accessToken, refreshToken), username);
        return "logout";
    }

    // 리프레시 토큰만 삭제된다
    @PostMapping("/refresh/delete")
    public @ResponseBody String refreshDelete(@RequestHeader("Authorization") String accessToken, 
                        @RequestHeader("RefreshToken") String refreshToken) {
        
        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
        mainServiceImpl.refreshTokenDelete(TokenDto.of(accessToken, refreshToken), username);
        return "success";
    }

    private String resolveToken(String accessToken) {
        return accessToken.substring(7);
    }
    

}
