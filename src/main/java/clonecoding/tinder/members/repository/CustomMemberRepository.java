package clonecoding.tinder.members.repository;

import clonecoding.tinder.members.dto.MemberSearch;
import clonecoding.tinder.members.entity.Member;

import java.util.List;

public interface CustomMemberRepository {

    List<Member> findAllWithoutLike(Long myId, int offset, int limit);
    List<Member> findAllWithoutPaging(Long myId, MemberSearch memberSearch);
}
