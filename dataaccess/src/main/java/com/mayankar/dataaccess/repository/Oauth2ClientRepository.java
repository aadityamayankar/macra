package com.mayankar.dataaccess.repository;

import com.mayankar.model.Oauth2Client;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Oauth2ClientRepository extends ReactiveCrudRepository<Oauth2Client, Long> {
    @Query("SELECT * FROM oauth2_client WHERE client_id = :clientId")
    Mono<Oauth2Client> findByClientId(String clientId);
}
