package com.mayankar.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TicketBookingRequest {
    private String ticketId;
    private String eventId;
    @Min(value = 1, message = "Minimum quantity should be 1")
    @Max(value = 10, message = "Maximum quantity should be 10")
    private Integer quantity;
}
