package clonecoding.tinder.like.repository;

import clonecoding.tinder.like.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Likes, Long> {
}
