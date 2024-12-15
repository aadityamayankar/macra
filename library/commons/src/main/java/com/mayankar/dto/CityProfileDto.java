package com.mayankar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mayankar.validation.Validator;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityProfileDto {
    private String id;
    private String name;
    private String country;
}
