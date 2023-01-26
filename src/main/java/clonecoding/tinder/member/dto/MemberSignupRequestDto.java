package clonecoding.tinder.member.dto;

import lombok.Getter;

@Getter
public class MemberSignupRequestDto {
    private String phoneNum;
    private String nickName;
    private String password;
    private String birthDate;
    private String profile;
}
