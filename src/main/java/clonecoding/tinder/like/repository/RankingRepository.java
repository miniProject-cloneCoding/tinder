package clonecoding.tinder.like.repository;

import clonecoding.tinder.like.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByLikedMember(Long LikedMember);

    List<Ranking> findTop3ByOrderByCountDesc();
}
