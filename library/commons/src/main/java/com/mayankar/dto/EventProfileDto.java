package com.mayankar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mayankar.validation.Validator;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventProfileDto {
    //@TODO: add a composite id check
    //@TODO: add constaints to db
    private String id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    @Size(max = Validator.MAX_LOCATION_LENGTH)
    private String location;
    private String cityId;
    private String cityName;
    private String cover;

    private List<TicketProfileDto> tickets;
}
