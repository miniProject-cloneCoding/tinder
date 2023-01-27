package clonecoding.tinder.members.repository;

import clonecoding.tinder.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    Optional<Member> findByPhoneNum(String phoneNum);
}
