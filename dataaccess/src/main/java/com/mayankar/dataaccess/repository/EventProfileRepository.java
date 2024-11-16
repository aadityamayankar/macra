package com.mayankar.dataaccess.repository;

import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.model.EventProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EventProfileRepository extends ReactiveCrudRepository<EventProfile, Long> {
    String getAllEventProfiles = "SELECT * FROM event_profile";

    @Query("SELECT * FROM event_profile")
    Flux<EventProfile> getAllEventProfiles();

    @Query("SELECT ep.*, cp.name as city_name FROM event_profile ep LEFT JOIN city_profile cp ON ep.city_id = cp.id WHERE ep.id = :id")
    Mono<EventProfileWithCity> getEventProfileById(Long id);

    @Query("SELECT * FROM event_profile WHERE name = :name AND city_id = :cityId")
    Mono<EventProfile> getEventProfileByNameAndCityId(String name, Long cityId);
}
