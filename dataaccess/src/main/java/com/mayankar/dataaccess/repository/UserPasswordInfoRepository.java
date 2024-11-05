package com.mayankar.dataaccess.repository;

import com.mayankar.model.UserPasswordInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserPasswordInfoRepository extends ReactiveCrudRepository<UserPasswordInfo, Long> {
    @Query("SELECT * FROM user_password_info WHERE user_id = :userId")
    public Mono<UserPasswordInfo> findByUserId(Long userId);
}
