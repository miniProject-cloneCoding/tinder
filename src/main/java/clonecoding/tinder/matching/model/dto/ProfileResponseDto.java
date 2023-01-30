package clonecoding.tinder.matching.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {

    private String yourName;
    private String yourProfile;
    private String myName;
    private String myProfile;
}
