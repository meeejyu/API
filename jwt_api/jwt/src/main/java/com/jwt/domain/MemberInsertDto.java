package com.jwt.domain;

import java.security.NoSuchAlgorithmException;

import com.jwt.comm.encryption.Encryption;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class MemberInsertDto {
    
    private long id;

    private String loginId;

    private String password;

    private String more_password;

    private String nickname;

    private String email;

    private String petYN;

    private String petName;

    private String petCategory;

    private String wantPet;

    public MemberInsertDto(
            String loginId,
            String password,
            String more_password,
            String nickname,
            String email,
            String petYN, String petName, String petCategory,
            String wantPet) {
        this.loginId = loginId;
        this.password = password;
        this.more_password = more_password;
        this.nickname = nickname;
        this.email = email;
        this.petYN = petYN;
        this.petName = petName;
        this.petCategory = petCategory;
        this.wantPet = wantPet;
    }

    public void encryptionPass(MemberInsertDto memberInsertDto) {

        try {
            Encryption enc = new Encryption();
            String encPass = enc.encrypt(memberInsertDto.getPassword());
            this.password = encPass;
            System.out.println("암호화 : " + this.password );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
