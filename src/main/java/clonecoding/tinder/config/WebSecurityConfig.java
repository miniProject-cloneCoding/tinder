package clonecoding.tinder.config;

import clonecoding.tinder.jwt.JwtAuthFilter;
import clonecoding.tinder.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용 및 resources 접근 허용 설정
        return (web) -> web.ignoring()
//                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                //멤버 api는 인증을 받지 않는다.
                .antMatchers("/member/**").permitAll()
                .antMatchers("/members/**").permitAll()
                //다른 것들은 전부 인증을 받아야 한다.
                .anyRequest().authenticated()
                // JWT 인증/인가를 사용하기 위한 설정
                // addFilterBefore -> 1번째 인자값의 필터가 2번째 인자값의 필터를 수행하기 전에 실행됨.
                // 즉, Jwt필터를 이용해 일단 토큰이 제대로 되어 있는 지부터 확인한다.
                .and().addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);


        //폼로그인 일단 꺼두기
//        http.formLogin().disable();

        //일단 폼로그인인지 뭔지 몰라서 주석처리
//        http.formLogin().loginPage("/member/login").permitAll();

        //혹시 forbidden 페이지 필요하다고 하면 살리기
//        http.exceptionHandling().accessDeniedPage("/forbidden");

        return http.build();
    }
}
