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

//    private final JPAQueryFactory queryFactory;
//
//    public CustomMemberRepositoryImpl(EntityManager em) {
//        queryFactory = new JPAQueryFactory(em);
//    }


//    @Override
//    public List<Member> findAllWithoutLike(Long myId, Long offset, int limit) {
//        return queryFactory.selectFrom(member)
//                .where(member.id.ne(myId), //나 자신은 제외
//                        member.id.notIn(  //내가 좋아요 한 사람도 제외
//                        (Number) JPAExpressions
//                                .select(likes.likedMember)
//                                .from(likes)
//                                .where(likes.likingMember.id.eq(myId))
//                ))
//                .offset(offset)
//                .limit(limit)
//                .fetch();
//    }

    @Override //전체 조회하기
    public List<Member> findAllWithoutLike(Long myId, int offset, int limit, MemberSearch memberSearch) {

        //조회할 회원 중 나 자신은 제외하고, 내가 이미 좋아요 한 회원도 제외함
        String jpql = "select m from Member m where m.id != :myId and m.id not in" +
                "(select l.likedMember from Likes l where l.likingMember = :myId2) ";

        boolean isFirstCondition = true;

        //내가 원하는 성별을 골라서 조회함(여자를 원하는 경우)
        if (memberSearch.isFemale()) {
            jpql += "and m.myGender in (0";
            isFirstCondition = false;
        }

        //남자를 원하는 경우
        if (memberSearch.isMale()) {
            if (isFirstCondition) {
                jpql += "and m.myGender in (1)";
            } else { //남녀 둘다 원하는 경우
                jpql += ", 1)";
            }
        } else {
            jpql += ")";
        }

        log.info("getMember jpql = {} ", jpql);

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

        log.info("getMember jpql = {} ", jpql);

        TypedQuery<Member> query = em.createQuery(jpql, Member.class);
        //파라미터 바인딩
        query.setParameter("myId", myId);
        query.setParameter("myId2", myId);

        return query.getResultList();
    }
}
