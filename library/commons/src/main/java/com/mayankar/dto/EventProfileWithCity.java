package com.mayankar.dto;

import com.mayankar.model.EventProfile;
import lombok.Data;

@Data
public class EventProfileWithCity extends EventProfile {
    private String cityName;
}
