package clonecoding.tinder.like.dto;

import clonecoding.tinder.like.service.LikeService;
import lombok.Getter;

@Getter
public class LikeResponseDto {
    private String msg;
    private Integer httpStatus;

    public LikeResponseDto(String msg, Integer httpStatus) {
        this.msg = msg;
        this.httpStatus = httpStatus;
    }
}
