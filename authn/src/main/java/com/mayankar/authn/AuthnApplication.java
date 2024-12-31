package com.mayankar.authn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@ComponentScan(basePackages = "com.mayankar")
@EnableR2dbcRepositories(basePackages = "com.mayankar.dataaccess.repository")
public class AuthnApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthnApplication.class, args);
    }

}
