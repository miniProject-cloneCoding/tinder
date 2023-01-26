package clonecoding.tinder.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponseMsgDto {
    private String msg;
    private Integer httpStatus;

    public MemberResponseMsgDto(String msg, Integer httpStatus) {
        this.msg = msg;
        this.httpStatus = httpStatus;
    }
}
