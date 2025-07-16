package org.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public HelloSpring helloSpring() {
        HelloSpring helloSpring = new HelloSpring();
        helloSpring.setMessage("Hello, Spring!");
        return helloSpring;
    }
}
