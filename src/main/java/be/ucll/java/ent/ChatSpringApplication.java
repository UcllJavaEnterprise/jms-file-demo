package be.ucll.java.ent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
public class ChatSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatSpringApplication.class, args);
    }
}
