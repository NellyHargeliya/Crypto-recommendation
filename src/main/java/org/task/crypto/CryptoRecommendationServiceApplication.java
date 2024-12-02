package org.task.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
public class CryptoRecommendationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoRecommendationServiceApplication.class, args);
    }

}
