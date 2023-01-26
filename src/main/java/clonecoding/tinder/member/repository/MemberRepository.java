package clonecoding.tinder.member.repository;

import clonecoding.tinder.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    Optional<Member> findByPhoneNum(String phoneNum);

    Page<Member> findAllByNickNameNot(String nickName, Pageable pageable);

    Optional<Member> findByNickName(String nickName);
}
