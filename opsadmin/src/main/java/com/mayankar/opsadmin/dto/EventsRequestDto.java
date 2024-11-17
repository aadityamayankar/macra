package com.mayankar.opsadmin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventsRequestDto {
    private String city;
    private String name;
    private String startDate;
    private String endDate;
}
