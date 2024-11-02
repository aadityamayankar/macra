package com.mayankar.dataaccess.repository;

import com.mayankar.model.RoleProfile;
import com.mayankar.model.UserRoleAssignment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRoleAssignmentRepository extends ReactiveCrudRepository<UserRoleAssignment, Long> {
    @Query("SELECT * FROM user_role_assignment WHERE user_id = :userId")
    Flux<UserRoleAssignment> findByUserId(Long userId);
}
