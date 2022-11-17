package com.jwt.domain;

import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.jwt.comm.encryption.Encryption;

import lombok.Getter;
import lombok.ToString;

/*
 * 벨리데이션 그룹을 사용하면 유효성 검사를 하는 순서는 할 수 있으나 모든 에러를 담을수는 없다
 * ex) 
 * NotBlank에서 에러가 나면 @Pattern에러는 담지 못함
 * @NotBlank는 "", " ", null 모두 잡음
 */
@Getter @ToString
public class MemberInsertDto {
    
    private long id;

    // NotBlank로 변경
    @NotBlank(message = "id_chk1")
    @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "id_chk2")
    private String loginId;

    @NotBlank(message = "pw_chk1")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "pw_chk2")
    private String password;

    @NotBlank(message = "pw_more_chk1")
    private String more_password;

    // 2~16자 제한 정규식 추가
    // ^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,16}$
    @NotBlank(message = "nick_chk1")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "nick_chk2")
    private String nickname;

    @NotBlank(message = "email_chk1")
    @Email(message = "email_chk2")
    private String email;

    @NotBlank(message = "petYN_chk1")
    private String petYN;

    // @NotEmpty
    private String petName;

    // @NotEmpty
    private String petCategory;

    @NotBlank(message = "want_pet_chk1")
    private String wantPet;

    public MemberInsertDto(
            @NotBlank(message = "id_chk1") @Pattern(regexp = "^([a-z]+[0-9]*)$", message = "id_chk2") String loginId,
            @NotBlank(message = "pw_chk1") @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "pw_chk2") String password,
            @NotBlank(message = "pw_more_chk1") String more_password,
            @NotBlank(message = "nick_chk1") @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "nick_chk2") String nickname,
            @NotBlank(message = "email_chk1") @Email(message = "email_chk2") String email,
            @NotBlank(message = "petYN_chk1") String petYN, String petName, String petCategory,
            @NotBlank(message = "want_pet_chk1") String wantPet) {
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
