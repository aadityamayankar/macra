package com.mayankar.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHistoryResponseDto {
    private String eventId;
    private String eventCover;
    private String eventName;
    private String eventStartDate;
    private String eventEndDate;
    private String eventLocation;
    private Double totalAmount;
    List<ImmutablePair<String, Integer>> tickets;
}
