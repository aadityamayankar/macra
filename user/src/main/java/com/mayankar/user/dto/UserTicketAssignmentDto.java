package com.mayankar.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mayankar.dto.EventProfileDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTicketAssignmentDto {
    private String id;
    private String userId;
    private String ticketId;
    private Integer quantity;
    private Status status;
    private EventProfileDto event;
}
