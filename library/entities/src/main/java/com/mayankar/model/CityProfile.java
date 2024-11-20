package com.mayankar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@Table("city_profile")
public class CityProfile extends BaseEntityProfile {
    @Id
    private Long id;
    private String name;
    private String country;
}
