package com.jocoweco.FoodSommelier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FoodSommelierApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodSommelierApplication.class, args);
    }

}
