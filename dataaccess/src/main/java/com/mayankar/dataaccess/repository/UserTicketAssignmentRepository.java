package com.mayankar.dataaccess.repository;

import com.mayankar.model.UserTicketAssignment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.MISC_FLAG_DELETED;

@Repository
public interface UserTicketAssignmentRepository extends ReactiveCrudRepository<UserTicketAssignment, Long>, BaseRepository {

    @Query("SELECT * FROM user_ticket_assignment WHERE user_id = :userId AND (miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Flux<UserTicketAssignment> getUserTicketAssignmentByUserId(Long userId);

    @Query("INSERT INTO user_ticket_assignment (user_id, ticket_id, event_id, quantity) VALUES (:userId, :ticketId, :eventId, :quantity)")
    Mono<UserTicketAssignment> insertUserTicketAssignment(Long userId, Long ticketId, Long eventId, Integer quantity);
}
