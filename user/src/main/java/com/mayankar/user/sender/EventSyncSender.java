package com.mayankar.user.sender;

import com.mayankar.proto.EventSyncMessage;
import com.mayankar.util.MessagingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.time.Instant;
import com.google.protobuf.Timestamp;

import static com.mayankar.util.Constants.MessagingConstants.EVENT_SYNC_EXCHANGE;
import static com.mayankar.util.Constants.MessagingConstants.EVENT_SYNC_ROUTING_KEY;

@Component
public class EventSyncSender {
    private static final Logger logger = LoggerFactory.getLogger(EventSyncSender.class);

    @Autowired
    private Sender sender;

    @Autowired
    private MessagingUtil messagingUtil;

    public static Timestamp fromInstant(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public Mono<Void> sendEventSyncMessage(String eventId, String ticketId, Integer quantityBooked, Instant timestamp) {
        EventSyncMessage.EventSync.Builder eventSync = EventSyncMessage.EventSync.newBuilder()
                .setEventId(eventId)
                .setTicketId(ticketId)
                .setQuantityBooked(quantityBooked)
                .setTimestamp(fromInstant(timestamp));

        byte[] eventSyncByteArray = eventSync.build().toByteArray();

        OutboundMessage message = messagingUtil.createOutBoundMessage(EVENT_SYNC_EXCHANGE, EVENT_SYNC_ROUTING_KEY, eventSyncByteArray);

        logger.info("Sending event sync message");

        return sender.send(Mono.just(message))
                .doOnSuccess(unused -> logger.info("Event sync message sent successfully"))
                .doOnError(throwable -> logger.error("Error while sending event sync message"));
    }
}
