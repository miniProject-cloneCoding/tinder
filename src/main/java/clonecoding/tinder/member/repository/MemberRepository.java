package clonecoding.tinder.member.repository;

import clonecoding.tinder.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
