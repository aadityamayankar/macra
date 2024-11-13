package com.mayankar.opsadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@ComponentScan(basePackages = "com.mayankar")
@EnableR2dbcRepositories(basePackages = "com.mayankar.dataaccess.repository")
public class OpsadminApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpsadminApplication.class, args);
    }

}
