package clonecoding.tinder.like.repository;

import clonecoding.tinder.like.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long>, CustomLikeRepository {
    Optional<Likes> findByLikedMemberAndLikingMember(Long likedMemberId, Long likingMemberId);

    Optional <List<Likes>> findAllByLikingMember(Long likingMemberId);

}
