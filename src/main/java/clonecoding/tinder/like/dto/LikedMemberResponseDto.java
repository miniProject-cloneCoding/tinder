package clonecoding.tinder.like.dto;

import clonecoding.tinder.members.entity.Member;
import lombok.Getter;

@Getter
public class LikedMemberResponseDto {
    private Long id;
    private String nickName;
    private String profile;
    private int age;

    public LikedMemberResponseDto(Member member, int age) {
        this.id = member.getId();
        this.nickName = member.getNickName();
        this.profile = member.getProfile();
        this.age =age;
    }
}
