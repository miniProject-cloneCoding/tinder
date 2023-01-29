package clonecoding.tinder.config;

import clonecoding.tinder.matching.model.Comments;
import clonecoding.tinder.security.UserDetailsImpl;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties; //applicaion.properties에 spring redis설정하면 RedisProperties를 제공해준다

    @Bean //ConnectionFactory 빈으로 등록
    public RedisConnectionFactory redisConnectionFactory() {

        //RedisProperties 에서 application.properties에서 설정한 URL을 가져온다
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());

        // URI를 가지고 Configuration을 만들어 준 다음에
        // Connection을 만들어주는 factory를 만들어 factory를 반환한다
        org.springframework.data.redis.connection.RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);

        //Lettuce는 전통적인 제디스보다 나중에 나왔고 성능이 더 좋아서 사용한다
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        // Initializing 된다
        factory.afterPropertiesSet();

        return factory;
    }

    @Bean             // <Key, Value>
    public RedisTemplate<String, UserDetailsImpl> userRedisTemplate() {

        //RedisTemplate을 Bean으로 만들어서 반환
        RedisTemplate<String, UserDetailsImpl> redisTemplate = new RedisTemplate<>();

        //너무 자주 변하는 데이터는 캐싱을 해도 별로
        //자주 사용하고 접근이 많은 데이터를 캐싱하는 것이 좋음(DB 부하가 적어지므로)
        // -> 그렇다면 가장 많이 사용하는 User 를 캐싱해 보는 것이 좋음 (매 API 마다 필터를 탈 때 DB에서 USER를 체크하니까)

        //작성한 코드를 실제 redis에 command 날릴수 있게 하려면 서버에 대한 정보를 알아야 하고
        // ConnectionFactory가 그 정보를 가지고 있다
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        //데이터를 저장할 때 serializer 해준다  (키 값이 String 이므로 StringRedisSerializer 사용했다)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //데이터를 저장할 때 serializer 해준다 (User는 오브젝트 이므로 Jackson2JsonRedisSerializer 사용했다)
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<UserDetailsImpl>(UserDetailsImpl.class));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<Long, Comments> matchingRedisTemplate() {
        RedisTemplate<Long, Comments> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        //숫자도 이렇게 serializer 해주어야 Redis CLI 창에서 제대로 키 값이 보인다
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Long.class));

        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Comments>(Comments.class));
        return redisTemplate;
    }
}
