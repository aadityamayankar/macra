package com.mayankar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@Table("event_profile")
public class EventProfile extends BaseEntityProfile {
    @Id
    private Long id;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private String location;
    private Long cityId;
}