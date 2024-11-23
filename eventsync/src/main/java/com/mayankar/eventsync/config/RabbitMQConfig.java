package com.mayankar.eventsync.config;

import com.mayankar.util.ConfigProps;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Mono<Connection> connectionMono(ConfigProps configProps) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setHost(configProps.getRabbitMQHost());
        connectionFactory.setPort(configProps.rabbitMQPort);
        connectionFactory.setUsername(configProps.getRabbitMQUsername());
        connectionFactory.setPassword(configProps.getRabbitMQPassword());
        return Mono.fromCallable(connectionFactory::newConnection);
    }

    @Bean
    public Sender sender(Mono<Connection> connectionMono) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
    }

    @Bean
    public Receiver receiver(Mono<Connection> connectionMono) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
    }
}
