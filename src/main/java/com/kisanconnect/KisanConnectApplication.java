package com.kisanconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(exclude = {
        FlywayAutoConfiguration.class
})
@EnableRetry
@EnableCaching
public class KisanConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(KisanConnectApplication.class, args);
    }
}
