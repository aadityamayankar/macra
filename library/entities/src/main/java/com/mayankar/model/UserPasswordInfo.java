package com.mayankar.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table("user_password_info")
public class UserPasswordInfo {
    @Id
    private Long id;
    private Long userId;
    private String passwordHash;
    private Instant modifiedAt;
    private Instant createdAt;
    private Long miscflags;
}
