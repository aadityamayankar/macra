package com.mayankar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("role_profile")
public class RoleProfile {
    @Id
    private Long id;

    private Integer value;

    private String name;

    private String description;
}
