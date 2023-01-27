package clonecoding.tinder.matching.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingDto {

    private Long likedMember;
    private Long likingMember;
}
