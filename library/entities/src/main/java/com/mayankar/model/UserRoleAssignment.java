package com.mayankar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("user_role_assignment")
public class UserRoleAssignment extends BaseEntityProfile {
    @Id
    private Long id;
    private Long userId;
    private Long roleId;
}
