package com.mayankar.eventsync.listener;

import com.google.protobuf.Timestamp;
import com.mayankar.dataaccess.cachedrepository.TicketProfileCacheRepository;
import com.mayankar.proto.EventSyncMessage;
import com.mayankar.util.Constants;
import com.rabbitmq.client.Delivery;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.time.Instant;

import static com.mayankar.util.Constants.MessagingConstants.EVENT_SYNC_QUEUE;

@Component
public class EventSyncListener {
    private static final Logger logger = LoggerFactory.getLogger(EventSyncListener.class);

    @Autowired
    private Receiver receiver;

    @Autowired
    TicketProfileCacheRepository ticketProfileCacheRepository;

    @PostConstruct
    public void init() {
        listen();
    }

    private void listen() {
        Flux<Delivery> messages = receiver.consumeAutoAck(EVENT_SYNC_QUEUE)
                .doOnError(e -> logger.error("Error receiving message", e));

        messages.flatMap(delivery -> {
            logger.info("Received message of size {} bytes from exchange {} with routing key {}",
                    delivery.getBody().length, delivery.getEnvelope().getExchange(), delivery.getEnvelope().getRoutingKey());
            return handleEventSyncMessage(delivery.getBody());
        }).subscribe();
    }

    private Mono<Boolean> handleEventSyncMessage(byte[] message) {
        EventSyncMessage.EventSync eventSync = null;
        try {
            eventSync = EventSyncMessage.EventSync.parseFrom(message);
            logger.info("Received event sync message: {}", eventSync);
        } catch (Exception e) {
            logger.error("Error parsing event sync message", e);
            return Mono.error(e);
        }

        EventSyncMessage.EventSync finalEventSync = eventSync;
        return ticketProfileCacheRepository.getTicketProfile(eventSync.getTicketId())
                .flatMap(ticketProfile -> {
                    ticketProfile.setAvailableQuantity(ticketProfile.getAvailableQuantity() - finalEventSync.getQuantityBooked());
                    ticketProfile.setModifiedAt(fromTimestamp(finalEventSync.getTimestamp()));
                    return ticketProfileCacheRepository.saveTicketProfile(ticketProfile);
                })
                .map(ticketProfile -> true)
                .defaultIfEmpty(false)
                .doOnSuccess(ticketProfile -> logger.info("Ticket profile updated successfully"))
                .doOnError(throwable -> logger.error("Error updating ticket profile"));
    }

    public static Instant fromTimestamp(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
