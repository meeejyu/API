package com.jwt.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.ToString;


@Getter @ToString
public class Member {
    
    private long id;

    private String loginId;

    private String password;

    private String username;

    private String more_password;

    private String nickname;

    private String email;

    private String petYN;

    private String petName;

    private String petCategory;

    private String wantPet;

    private Set<Authority> authorities = new HashSet<>();

    // 회원가입을 위한 MemberInsertDto를 member로 변환
    public static Member ofUser(MemberInsertDto memberInsertDto) {
        Member member = new Member(
            memberInsertDto.getId(), 
            memberInsertDto.getLoginId(), 
            memberInsertDto.getPassword(),
            memberInsertDto.getNickname(),
            memberInsertDto.getEmail(), 
            memberInsertDto.getPetYN(), 
            memberInsertDto.getPetName(), 
            memberInsertDto.getPetCategory(), 
            memberInsertDto.getWantPet());
        member.addAuthority(Authority.ofUser(member.getId()));
        member.createUsername();
        return member;
    }

    private void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    // 회원 값 가져올떄 사용
    public Member(long id,
            String loginId,
            String password,
            String nickname,
            String email,
            String petYN, String petName, String petCategory,
            String wantPet) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.petYN = petYN;
        this.petName = petName;
        this.petCategory = petCategory;
        this.wantPet = wantPet;
    }

    public List<String> getRoles() {
        return authorities.stream()
                .map(Authority::getRole)
                .collect(Collectors.toList());
    }

    public String createUsername() {
        username = UUID.randomUUID().toString(); 
        return username;
    }

}
