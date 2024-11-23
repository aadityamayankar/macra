package com.mayankar.eventsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableScheduling
@ComponentScan(basePackages = "com.mayankar")
@EnableR2dbcRepositories(basePackages = "com.mayankar.dataaccess.repository")
public class EventSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventSyncApplication.class, args);
    }

}
