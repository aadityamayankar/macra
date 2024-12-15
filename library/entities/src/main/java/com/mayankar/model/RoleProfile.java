package com.mayankar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@Table("role_profile")
public class RoleProfile extends BaseEntityProfile {
    @Id
    private Long id;
    private Integer value;
    private String name;
    private String description;
}
