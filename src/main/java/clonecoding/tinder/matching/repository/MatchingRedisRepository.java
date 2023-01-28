package clonecoding.tinder.matching.repository;

import clonecoding.tinder.matching.model.Comments;
import clonecoding.tinder.security.UserDetailsImpl;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchingRedisRepository {

    private final RedisTemplate<Long, Comments> redisTemplate;
    private final static Duration COMMENT_CACHE_TTL = Duration.ofDays(30); //30일 지난 데이터 삭제

    public void setComments(Comments comments, Long roomId) {
        redisTemplate.opsForValue().set(roomId, comments, COMMENT_CACHE_TTL);
    }

    public Optional<Comments> getComments(Long roomId) {
        Comments comments = redisTemplate.opsForValue().get(roomId);
        return Optional.ofNullable(comments);
    }

}


