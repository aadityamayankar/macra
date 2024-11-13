package com.mayankar.opsadmin.dto;

import lombok.Data;

@Data
public class EventProfileDto {
    private Long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String location;
    private Long cityId;
}
