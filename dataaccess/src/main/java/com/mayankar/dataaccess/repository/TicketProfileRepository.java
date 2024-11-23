package com.mayankar.dataaccess.repository;

import com.mayankar.model.TicketProfile;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.mayankar.util.Constants.MISC_FLAG_DELETED;

@Repository
public interface TicketProfileRepository extends ReactiveCrudRepository<TicketProfile, Long>, BaseRepository {

    @Query("UPDATE ticket_profile SET miscflags = miscflags | " + MISC_FLAG_DELETED + " WHERE event_id = :eventId")
    Mono<Void> deleteTicketProfilesByEventId(Long eventId);

    @Query("SELECT * FROM ticket_profile WHERE event_id = :eventId AND (miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Flux<TicketProfile> getTicketProfilesByEventId(Long eventId);

    @Query("SELECT * FROM ticket_profile WHERE event_id = :eventId")
    Flux<TicketProfile> getAllTicketProfilesByEventId(Long eventId);

    @Query("UPDATE ticket_profile SET miscflags = miscflags | " + MISC_FLAG_DELETED + " WHERE id = :id")
    Mono<Void> deleteTicketProfileById(Long id);

    @Query("SELECT * FROM ticket_profile WHERE modified_at > :instant")
    Flux<TicketProfile> getAllTicketProfilesUpdatedAfter(Instant instant);
}
