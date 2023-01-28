package clonecoding.tinder.matching.model.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {

    private Long oppositeMember;
    private Long roomId;
    private String content;
}
