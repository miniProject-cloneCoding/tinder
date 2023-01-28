package clonecoding.tinder.members.repository;

import clonecoding.tinder.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRedisRepository {

    private final RedisTemplate<String, UserDetailsImpl> userRedisTemplate;

    // 레디스는 TTL 걸어주는 것이 좋다 (일정 시간이 지나면 expire)
    // 사용하지 않는 캐시가 계속 남아있지 않도록 예방해주는 장점이 있다
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);


    public void setUser(UserDetailsImpl user) {

        //유저이름을 가지고 키를 만들어준다
        String key = getKey(user.getPhoneNum());
        log.info("Redis에 회원저장 {}({})", key, user);

        // 데이터가 올라간 시점으로부터 3일동안만 유효
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    public Optional<UserDetailsImpl> getUser(String phoneNum) {
        UserDetailsImpl user = userRedisTemplate.opsForValue().get(getKey(phoneNum));
        log.info("Redis에서 회원조회 {}", user);
        return Optional.ofNullable(user);
    }

    private String getKey(String phoneNum) {

        // Redis에서 키 값을 구성할 떄 앞에 prefix 붙여주는 것이 좋다
        // 아래와 같이 USER 를 붙이면 User에 관한 정보라는 것을 키값만 보고도 알 수 있다
        return "USER:" + phoneNum;
    }
}