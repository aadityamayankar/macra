package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.model.TicketReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;

import static com.mayankar.util.CacheConstants.*;

@Repository
public class TicketReservationRepository {
    private static final Logger logger = LoggerFactory.getLogger(TicketReservationRepository.class);

    @Autowired
    ReactiveRedisOperations<String, TicketReservation> reactiveRedisOperations;

    public Mono<Void> createTicketReservation(String key, TicketReservation ticketReservation) {
        return reactiveRedisOperations.opsForList().rightPush(key, ticketReservation)
                .then();
    }

    public Mono<TicketReservation> getTicketReservationById(String key, String ticketReservationId) {
        return reactiveRedisOperations.opsForList().range(key, 0, -1)
                .filter(ticketReservation -> ticketReservation.getId().equals(ticketReservationId))
                .next();
    }

    public Mono<Integer> getReservedQuantity(String key) {
        return reactiveRedisOperations.opsForList().range(key, 0, -1)
                .filter(ticketReservation -> !isTicketReservationExpired(ticketReservation))
                .map(TicketReservation::getReservedQuantity)
                .reduce(0, Integer::sum);
    }

    public Mono<Long> cleanUpExpiredTicketReservations(String key) {
        return reactiveRedisOperations.opsForList().range(key, 0, -1)
                .filter(this::isTicketReservationExpired)
                .flatMap(ticketReservation -> reactiveRedisOperations.opsForList().remove(key, 1, ticketReservation))
                .count();
    }

    public Mono<Boolean> deleteTicketReservation(String key, String ticketReservationId) {
        logger.info("Deleting ticket reservation with id: {}", ticketReservationId);
        return reactiveRedisOperations.opsForList().range(key, 0, -1)
                .filter(ticketReservation -> ticketReservation.getId().equals(ticketReservationId))
                .flatMap(ticketReservation -> reactiveRedisOperations.opsForList().remove(key, 1, ticketReservation))
                .hasElements();
    }

    private boolean isTicketReservationExpired(TicketReservation ticketReservation) {
        return ticketReservation.getCreatedAt().isBefore(Instant.now().minusSeconds(TICKET_RESERVATION_EXPIRY));
    }

    public String getTicketReservationKey(String eventId, String ticketId) {
        return MessageFormat.format(TICKET_RESERVATION_KEY, eventId, ticketId);
    }
}
