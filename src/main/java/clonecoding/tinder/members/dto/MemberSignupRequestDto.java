package clonecoding.tinder.members.dto;

import lombok.Getter;

@Getter
public class MemberSignupRequestDto {
    private String phoneNum;
    private String nickName;
    private String password;
    private String birthDate;
    private String profile;
    private double latitude;
    private double longitude;
    private int myGender; //0 여자 , 1 남자
    private boolean wantingMale; //여자를 원하는지 여부
    private boolean wantingFemale; //남자를 원하는지 여부
}
