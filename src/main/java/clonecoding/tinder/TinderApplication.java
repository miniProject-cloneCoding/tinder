package clonecoding.tinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinderApplication.class, args);
    }

}
