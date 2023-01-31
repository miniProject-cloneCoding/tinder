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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //스웨거 관련 url
    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };



    //webSecurityCustomizer() 이거 안 써도 됩니다 그냥 남겨둔 것
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용 및 resources 접근 허용 설정
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //CSRF(Cross-Site Request Forgery) 보호를 비활성화
        http.csrf().disable();

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //다양한 엔드포인트 패턴에 대한 요청을 승인
        http.authorizeRequests()
                //URL이 "/member/" 및 "/members/"인 엔드포인트는 인증 없이 모두에게 개방
                //URL이 "/upload"인 엔드포인트는 인증 없이 모두에게 공개.
                .antMatchers("/member/**").permitAll()
                .antMatchers("/members/**").permitAll()
                .antMatchers("/upload").permitAll()


                //swagger 관련해서 인증 통과
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                //다른 것들은 전부 인증을 받아야 한다.
                .anyRequest().authenticated()
                // JWT 인증/인가를 사용하기 위한 설정
                // addFilterBefore -> 1번째 인자값의 필터가 2번째 인자값의 필터를 수행하기 전에 실행됨.

                // 즉, Jwt필터를 이용해 일단 토큰이 제대로 되어 있는 지부터 확인한다.
                // 정리하면 인증 및 권한 부여를 처리하기 전에 먼저 토큰의 유효성을 검사하는 것.
                .and().addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        //http 보안 설정을 위 내용으로 해서 반환
        return http.build();
    }

    /*
    이렇게 고칠 수도?
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .authorizeRequests()
                .antMatchers("/member/**", "/members/**", "/upload").permitAll()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .anyRequest().authenticated()
            .and().addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000"); //프론트가 3000번 포트를 사용함
        configuration.setAllowCredentials(true); //이 서버의 응답을 js가 처리할 수 있다
        configuration.addAllowedMethod("*"); // 모든 메소드 허용
        configuration.addAllowedHeader("*"); // 헤더 허용
        configuration.addExposedHeader("Authorization"); // 헤더에 Authorization 허용
        configuration.addAllowedOriginPattern("*");


        //registerCorsConfiguration 메서드를 통해 CORS 구성을 등록함. 이 메서드는 두 개의 인자를 가지는데,
        //첫 번째에는 구성을 적용할 URL, 두 번째에는 이전에 실제 CORS 구성이 적용된 CorsConfiguration 객체가 들어간다.
        //즉, 여기에서는 configuration 객체에 지정된 설정을 사용하여 첫번째 인자의 URL에 대한 CORS 구성을 설정한다는 것.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
