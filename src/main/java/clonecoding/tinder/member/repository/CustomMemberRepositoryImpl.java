package clonecoding.tinder.member.repository;

import clonecoding.tinder.member.entity.Member;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static clonecoding.tinder.like.entity.QLikes.likes;
import static clonecoding.tinder.member.entity.QMember.member;

public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory queryFactory;

    public CustomMemberRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<Member> findAllWithoutLike(Long myId, Long offset, int limit) {
        return queryFactory.selectFrom(member)
                .where(member.id.ne(myId), //나 자신은 제외
                        member.id.notIn(  //내가 좋아요 한 사람도 제외
                        (Number) JPAExpressions
                                .select(likes.likedMember)
                                .from(likes)
                                .where(likes.likingMember.id.eq(myId))
                ))
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}
