package com.mayankar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("user_role_assignment")
public class UserRoleAssignment {
    @Id
    private Long id;

    private Long userId;

    private Long roleId;
}
