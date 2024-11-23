package com.mayankar.util;

import com.rabbitmq.client.AMQP;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.OutboundMessage;

import static com.mayankar.util.Constants.MessagingConstants.PROTOBUF_CONTENT_TYPE;

@Component
public class MessagingUtil {
    public OutboundMessage createOutBoundMessage(String exchange, String routingKey, byte[] message) {
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .contentType(PROTOBUF_CONTENT_TYPE)
                .build();
        return new OutboundMessage(exchange, routingKey,  message);
    }
}
