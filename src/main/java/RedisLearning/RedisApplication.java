package RedisLearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = "RedisLearning")
public class RedisApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RedisApplication.class);
        springApplication.run(args);
    }
}
