package clonecoding.tinder.members.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

@Getter
@Setter
@Slf4j
public class MemberResponseMsgDto {
    private String msg;
    private Integer httpStatus;

    public MemberResponseMsgDto(String msg, Integer httpStatus) {
        this.msg = msg;
        this.httpStatus = httpStatus;
    }


}
