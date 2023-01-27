package clonecoding.tinder.matching.model.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatchingDto {

    private Long likedMember;
    private Long likingMember;
}
