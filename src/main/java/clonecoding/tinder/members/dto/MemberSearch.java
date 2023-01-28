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

    public MemberSearch(boolean male, boolean female) {
        this.male = male;
        this.female = female;
    }
}
