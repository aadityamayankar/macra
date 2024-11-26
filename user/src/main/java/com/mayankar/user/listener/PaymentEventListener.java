package com.mayankar.user.listener;

import com.mayankar.proto.PaymentEventMessage;
import com.mayankar.user.service.PaymentEventProcessorService;
import com.rabbitmq.client.Delivery;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import static com.mayankar.util.Constants.MessagingConstants.PAYMENT_EVENT_QUEUE;

@Component
public class PaymentEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);

    @Autowired
    private Receiver receiver;
    @Autowired
    private PaymentEventProcessorService paymentEventProcessorService;

    @PostConstruct
    public void init() {
        listen();
    }

    private void listen() {
        Flux<Delivery> messages = receiver.consumeAutoAck(PAYMENT_EVENT_QUEUE)
                .doOnError(e -> logger.error("Error receiving message", e));

        messages.flatMap(delivery -> {
            logger.info("Received message of size {} bytes from exchange {} with routing key {}",
                    delivery.getBody().length, delivery.getEnvelope().getExchange(), delivery.getEnvelope().getRoutingKey());
            return handlePaymentEventMessage(delivery.getBody());
        }).subscribe();
    }

    private Mono<?> handlePaymentEventMessage(byte[] message) {
        PaymentEventMessage.PaymentEvent paymentEvent = null;
        try {
            paymentEvent = PaymentEventMessage.PaymentEvent.parseFrom(message);
            logger.info("Received payment event message: {}", paymentEvent);
        } catch (Exception e) {
            logger.error("Error parsing payment event message", e);
            return Mono.error(e);
        }

        PaymentEventMessage.PaymentEvent finalPaymentEvent = paymentEvent;
        return paymentEventProcessorService.processEvent(finalPaymentEvent.getEventType(), finalPaymentEvent.getEventPayload())
                .doOnSuccess(success -> logger.info("Processed payment event message"))
                .doOnError(e -> logger.error("Error processing payment event message", e));
    }

}
