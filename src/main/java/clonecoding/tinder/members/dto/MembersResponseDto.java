package clonecoding.tinder.members.dto;

import clonecoding.tinder.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembersResponseDto {

    private Long id;
    private String nickName;
    private String birthDate;
    private String profile;
    private double latitude;
    private double longitude;
    private double distance;


    public MembersResponseDto(Member member) {
        id = member.getId();
        nickName = member.getNickName();
        birthDate = member.getBirthDate();
        profile = member.getProfile();
        latitude = member.getLatitude();
        longitude = member.getLongitude();
    }

    public static MembersResponseDto fromEntity(Member member) {
        return new MembersResponseDto(member);
    }
}
