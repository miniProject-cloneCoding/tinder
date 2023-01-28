package clonecoding.tinder.like.repository;

import clonecoding.tinder.like.entity.Likes;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@RequiredArgsConstructor
public class CustomLikeRepositoryImpl implements CustomLikeRepository{

    @PersistenceContext
    private final EntityManager em;

    @Override //내가 좋아요 누른 리스트
    public List<Likes> findMyLiking(Long myId) {
        TypedQuery<Likes> query = em.createQuery("select l from Likes l where l.likingMember = :myId", Likes.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        return query.getResultList();
    }

    @Override //내가 좋아요 받은 리스트
    public List<Likes> findMyLiked(Long myId) {
        TypedQuery<Likes> query = em.createQuery("select l from Likes l where l.likedMember = :myId", Likes.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        return query.getResultList();
    }

    @Override
    public List<Likes> findByLikingAndLiked(Long id1, Long id2) {
        TypedQuery<Likes> query = em.createQuery("select l from Likes l where l.likedMember = :id1 and l.likingMember = :id2", Likes.class);

        //파라미터 바인딩
        query.setParameter("id1", id1);
        query.setParameter("id2", id2);

        return query.getResultList();

    }
}
