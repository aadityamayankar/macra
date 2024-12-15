package com.mayankar.user.sender;

import com.mayankar.proto.PaymentEventMessage;
import com.mayankar.util.MessagingUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import static com.mayankar.util.Constants.MessagingConstants.PAYMENT_EVENT_EXCHANGE;
import static com.mayankar.util.Constants.MessagingConstants.PAYMENT_EVENT_ROUTING_KEY;

@Component
public class PaymentEventSender {
    private static final Logger logger = LoggerFactory.getLogger(PaymentEventSender.class);

    @Autowired
    private Sender sender;

    @Autowired
    private MessagingUtil messagingUtil;

    public Mono<Void> sendPaymentEventMessage(String eventJsonString) {

        JSONObject event = new JSONObject(eventJsonString);
        JSONObject eventPayload = event.getJSONObject("payload");
        String eventType = event.getString("event");
        JSONObject payment = eventPayload.getJSONObject("payment");
        JSONObject entity = payment.getJSONObject("entity");

        PaymentEventMessage.PaymentEvent.Builder paymentEvent = PaymentEventMessage.PaymentEvent.newBuilder()
                .setEventType(eventType)
                .setEventPayload(eventPayload.toString());

        byte [] paymentEventByteArray = paymentEvent.build().toByteArray();

        OutboundMessage message = messagingUtil.createOutBoundMessage(PAYMENT_EVENT_EXCHANGE, PAYMENT_EVENT_ROUTING_KEY, paymentEventByteArray);

        logger.info("Sending payment event message of type: {}", eventType);

        return sender.send(Mono.just(message))
                .doOnSuccess(unused -> logger.info("Payment event message sent successfully"))
                .doOnError(throwable -> logger.error("Error while sending payment event message"));
    }
}
