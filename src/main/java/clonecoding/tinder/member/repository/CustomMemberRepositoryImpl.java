package clonecoding.tinder.member.repository;

import clonecoding.tinder.member.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    @PersistenceContext
    private EntityManager em;

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

    @Override
    public List<Member> findAllWithoutLike(Long myId, int offset, int limit) {

        //조회할 회원 중 나 자신은 제외하고, 내가 이미 좋아요 한 회원도 제외함
        TypedQuery<Member> query = em.createQuery("select m from Member m where m.id != :myId and m.id not in " +
                "(select l.likedMember from Likes l where l.likingMember = :myId2) ", Member.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        query.setParameter("myId2", myId);

        //페이징 처리
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public List<Member> findAllWithoutPaging(Long myId) {
        //조회할 회원 중 나 자신은 제외하고, 내가 이미 좋아요 한 회원도 제외함
        TypedQuery<Member> query = em.createQuery("select m from Member m where m.id != :myId and m.id not in " +
                "(select l.likedMember from Likes l where l.likingMember = :myId2) ", Member.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        query.setParameter("myId2", myId);

        return query.getResultList();
    }

}
