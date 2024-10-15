package com.mayankar.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("user")
public class User {

    @Id
    private Long id;

    private String name;

    private String email;

    private String password;
}
