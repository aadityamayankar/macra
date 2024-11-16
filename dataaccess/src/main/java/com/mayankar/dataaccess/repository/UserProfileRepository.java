package com.mayankar.dataaccess.repository;

import com.mayankar.model.UserProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Long> {

    @Query("SELECT * FROM user_profile WHERE name = :name")
    Flux<UserProfile> findByName(String name);

    @Query("SELECT * FROM user_profile WHERE email = :email")
    Mono<UserProfile> findByEmail(String email);

    @Query("SELECT * FROM user_profile WHERE id = :id")
    Mono<UserProfile> getUserProfileById(Long id);
}
