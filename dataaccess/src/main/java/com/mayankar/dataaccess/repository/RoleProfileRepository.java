package com.mayankar.dataaccess.repository;

import com.mayankar.model.RoleProfile;
import com.mayankar.model.UserProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoleProfileRepository extends ReactiveCrudRepository<RoleProfile, Long> {

    @Query("SELECT * FROM role_profile WHERE value = :value")
    Mono<RoleProfile> getRoleProfileByValue(Integer value);

    @Query("SELECT * FROM role_profile rp JOIN user_role_assignment ura ON rp.id = ura.role_id WHERE ura.user_id = :userId")
    Mono<RoleProfile> getRoleProfileByUserId(Long userId);
}
