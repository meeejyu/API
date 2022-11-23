package com.jwt.main.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jwt.comm.util.JwtTokenUtil;
import com.jwt.domain.Authority;
import com.jwt.domain.LoginDto;
import com.jwt.domain.MemberInsertDto;
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

    @GetMapping("/member")
    public @ResponseBody List<Map<String, Object>> member() {
        List<Map<String, Object>> memberMap = mainService.getMemberList();

        System.out.println(memberMap.toString());
        // return "main";
        return memberMap;

    }

    @PostMapping("/signup/success")
    public @ResponseBody Map<String, Object> signUp_success(@Valid MemberInsertDto memberInsertDto, BindingResult bindingResult, Model model) {

        memberInsertDto.encryptionPass(memberInsertDto);

        mainService.memberSave(memberInsertDto);

        Map<String,Object> memberMap = mainService.memberLoginId(memberInsertDto.getLoginId());

        Authority authority = Authority.ofUser((Long.valueOf(memberMap.get("MEMBER_ID").toString())));

        mainService.authoritySave(authority);
        
        // return "signUpSuccess";
        return memberMap;

    }

    @PostMapping("/member/idCheck")
    public @ResponseBody String memberidCheck(String LOGIN_ID) {
        
        System.out.println("값이 잘오나 확인"+LOGIN_ID);
        Map<String,Object> memberMap = mainService.memberLoginId(LOGIN_ID);


        if(memberMap==null) {
            System.out.println("성공");
            return "success";            
        }
        else {
            Map<String,Object> memberMap2 = mainService.memberUsername(memberMap.get("MEMBER_USERNAME").toString());
            System.out.println("map 값 확인 : "+ memberMap2.toString());
            System.out.println("실패");
            return "fail";
        }
    }

    @PostMapping("/logout")
    public @ResponseBody String logout(@RequestHeader("Authorization") String accessToken, 
                        @RequestHeader("RefreshToken") String refreshToken) {
        
        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
        mainServiceImpl.logout(TokenDto.of(accessToken, refreshToken), username);
        return "logout";
    }

    private String resolveToken(String accessToken) {
        return accessToken.substring(7);
    }
    

}
