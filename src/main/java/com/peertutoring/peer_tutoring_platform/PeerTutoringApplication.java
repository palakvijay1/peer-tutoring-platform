package com.peertutoring.peer_tutoring_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.peertutoring")
@EntityScan("com.peertutoring")
@EnableJpaRepositories("com.peertutoring")
public class PeerTutoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeerTutoringApplication.class, args);
    }
}