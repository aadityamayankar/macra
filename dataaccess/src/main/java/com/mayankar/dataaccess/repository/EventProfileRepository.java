package com.mayankar.dataaccess.repository;

import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.model.EventProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.MISC_FLAG_DELETED;

@Repository
public interface EventProfileRepository extends ReactiveCrudRepository<EventProfile, Long>, BaseRepository {
    String getAllEventProfiles = "SELECT * FROM event_profile";

    String getAllEventProfilesWithCity = "SELECT ep.*, cp.name as city_name FROM event_profile ep LEFT JOIN city_profile cp ON ep.city_id = cp.id";

    String withName = " ep.name = :name ";

    String withCityName = " cp.name = :city ";

    String withStartDate = " ep.start_date >= :startDate ";

    String withEndDate = " ep.end_date <= :endDate ";

    String withDeleted = " (ep.miscflags & " + MISC_FLAG_DELETED + ") = " + MISC_FLAG_DELETED;

    String withNotDeleted = " (ep.miscflags & " + MISC_FLAG_DELETED + ") = 0 ";

    @Query("SELECT ep.*, cp.name as city_name FROM event_profile ep LEFT JOIN city_profile cp ON ep.city_id = cp.id WHERE ep.id = :id AND (ep.miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Mono<EventProfileWithCity> getEventProfileById(Long id);

    @Query("SELECT * FROM event_profile WHERE name = :name AND city_id = :cityId")
    Mono<EventProfile> getAllEventProfileByNameAndCityId(String name, Long cityId);

    @Query("UPDATE event_profile SET miscflags = miscflags | " + MISC_FLAG_DELETED + " WHERE id = :id")
    Mono<Void> deleteEventProfile(Long id);
}
