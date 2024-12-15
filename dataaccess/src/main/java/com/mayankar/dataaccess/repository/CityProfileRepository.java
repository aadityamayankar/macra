package com.mayankar.dataaccess.repository;

import com.mayankar.model.CityProfile;
import com.mayankar.model.TicketProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.mayankar.util.Constants.MISC_FLAG_DELETED;

@Repository
public interface CityProfileRepository extends ReactiveCrudRepository<CityProfile, Long>, BaseRepository {

    @Query("SELECT * FROM city_profile WHERE (miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Flux<CityProfile> getAllCities();

    @Query("SELECT * FROM city_profile WHERE id = :id AND (miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Mono<CityProfile> getCityProfileById(Long id);
}
