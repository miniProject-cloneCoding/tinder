package clonecoding.tinder.members.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberFindRequestDto {

    private Long id;
    private boolean like = false;
}
