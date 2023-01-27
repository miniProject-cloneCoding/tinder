package clonecoding.tinder.matching.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long sender;
    private String content;
    private LocalDateTime createdAt;
}
