package clonecoding.tinder.member.repository;

import clonecoding.tinder.member.entity.Member;

import java.util.List;

public interface CustomMemberRepository {

    List<Member> findAllWithoutLike(Long myId, int offset, int limit);
}
