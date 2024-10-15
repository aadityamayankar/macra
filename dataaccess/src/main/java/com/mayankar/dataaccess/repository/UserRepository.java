package com.mayankar.dataaccess.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import com.mayankar.dataaccess.model.User;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("SELECT * FROM \"user\" WHERE name = :name")
    Flux<User> findByName(String name);
}
