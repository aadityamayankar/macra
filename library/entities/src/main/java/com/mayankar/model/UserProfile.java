package com.mayankar.model;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

import java.time.Instant;

@Data
@NoArgsConstructor
@Table("user_profile")
public class UserProfile extends BaseEntityProfile {
    @Id
    private Long id;
    private String name;
    private String email;
}
