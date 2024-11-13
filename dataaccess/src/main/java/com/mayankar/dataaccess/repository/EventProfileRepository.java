package com.mayankar.dataaccess.repository;

import com.mayankar.model.EventProfile;
import com.mayankar.util.Constants;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EventProfileRepository extends ReactiveCrudRepository<EventProfile, Long> {
    String getAllEventProfiles = "SELECT * FROM event_profile";

//    @Query("UPDATE event_profile SET miscflags = miscflags | " + Constants.DELETED_MISCFLAG + " WHERE id = :id")
//    Mono<EventProfile> deleteEventProfile(Long id);
}
