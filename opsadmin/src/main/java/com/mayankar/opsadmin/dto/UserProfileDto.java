package com.mayankar.opsadmin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mayankar.validation.Validator;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {
    private String name;
    private String email;
}
