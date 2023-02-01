package clonecoding.tinder.members.repository;

import clonecoding.tinder.members.dto.MemberSearch;
import clonecoding.tinder.members.entity.Member;

import java.util.List;

public interface CustomMemberRepository {

    List<Member> findAllWithPaging(Long myId, MemberSearch memberSearch);
    List<Member> findAllWithoutPaging(Long myId, MemberSearch memberSearch);
}
