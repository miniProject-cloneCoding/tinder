package clonecoding.tinder.matching.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResponseDto {

    private Long memberId;
    private String nickName;
    private String profile;
    private int distance;
    private int age;
    private Long roomId;
}
