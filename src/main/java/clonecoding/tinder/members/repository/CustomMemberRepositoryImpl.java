package clonecoding.tinder.members.repository;

import clonecoding.tinder.members.dto.MemberSearch;
import clonecoding.tinder.members.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override //전체 조회하기
    public List<Member> findAllWithPaging(Long myId, int offset, int limit, MemberSearch memberSearch) {

        //조회할 회원 중 나 자신은 제외하고, 내가 이미 좋아요 한 회원도 제외함
        String jpql = "select m from Member m where m.id != :myId and m.id not in" +
                "(select l.likedMember from Likes l where l.likingMember = :myId2) ";

        boolean isFirstCondition = true;

        //내가 원하는 성별을 골라서 조회함(여자를 원하는 경우)
        if (memberSearch.isFemale()) {
            jpql += "and m.myGender in (0";
            isFirstCondition = false;
        }

        if (memberSearch.isMale()) {
            if (isFirstCondition) {  //남자만 원하는 경우
                jpql += "and m.myGender in (1)";
            } else { //남녀 둘다 원하는 경우
                jpql += ", 1)";
            }
        } else {
            jpql += ")";
        }

        log.info("회원 전체 조회(페이징) jpql = {} ", jpql);

        TypedQuery<Member> query = em.createQuery(jpql, Member.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        query.setParameter("myId2", myId);

        return query.getResultList();
    }

    @Override //페이징 없이 전체 조회하기
    public List<Member> findAllWithoutPaging(Long myId, MemberSearch memberSearch) {

        String jpql = "select m from Member m where m.id != :myId and m.id not in " +
                "(select l.likedMember from Likes l where l.likingMember = :myId2) ";

        boolean isFirstCondition = true;

        //내가 원하는 성별을 골라서 조회함(여자를 원하는 경우)
        if (memberSearch.isFemale()) {
            jpql += "and m.gender in (0";
            isFirstCondition = false;
        }

        if (memberSearch.isMale()) { //남자를 원하는 경우
            if (isFirstCondition) {
                jpql += "and m.gender in (1)";
            } else { //남녀 모두 원하는 경우
                jpql += ", 1)";
            }
        } else {
            jpql += ")";
        }

        log.info("회원 전체 조회 jpql = {} ", jpql);

        TypedQuery<Member> query = em.createQuery(jpql, Member.class);
        //파라미터 바인딩
        query.setParameter("myId", myId);
        query.setParameter("myId2", myId);

        return query.getResultList();
    }
}
