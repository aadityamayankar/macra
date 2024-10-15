package com.mayankar.dataaccess.model;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

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
