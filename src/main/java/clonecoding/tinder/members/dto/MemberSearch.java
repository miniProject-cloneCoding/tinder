package clonecoding.tinder.members.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberSearch {

    private boolean female;
    private boolean male;

    public MemberSearch(String[] wantedGender) {
        if (wantedGender.length == 2) { // 두 성별 모두 원하는 경우
            female = true;
            male = true;
        } else if (wantedGender[0].equals("0")) { //여자만 원하는 경우
            female = true;
            male = false;
        } else {  //남자만 원하는 경우
            female = false;
            male = true;
        }
    }
}
