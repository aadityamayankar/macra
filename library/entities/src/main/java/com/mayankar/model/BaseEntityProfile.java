package com.mayankar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntityProfile {
    private Instant modifiedAt;
    private Instant createdAt;
    private Long miscflags;
}
