package com.mayankar.authz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@ComponentScan(basePackages = "com.mayankar")
@EnableR2dbcRepositories(basePackages = "com.mayankar.dataaccess.repository")
public class AuthzApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthzApplication.class, args);
    }

}
