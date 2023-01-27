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

    @Override
    public List<Likes> findMyLiking(Long myId) {
        TypedQuery<Likes> query = em.createQuery("select l from Likes l where l.likingMember = :myId", Likes.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        return query.getResultList();
    }

    @Override
    public List<Likes> findMyLiked(Long myId) {
        TypedQuery<Likes> query = em.createQuery("select l from Likes l where l.likedMember = :myId", Likes.class);

        //파라미터 바인딩
        query.setParameter("myId", myId);
        return query.getResultList();
    }
}
