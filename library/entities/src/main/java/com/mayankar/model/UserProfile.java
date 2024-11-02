package com.mayankar.model;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@NoArgsConstructor
@Table("user_profile")
public class UserProfile {
    @Id
    private Long id;

    private String name;

    private String email;
}
