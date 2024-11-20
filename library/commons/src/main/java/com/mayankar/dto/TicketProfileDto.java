package com.mayankar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mayankar.validation.Validator;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketProfileDto {
    private String id;
    private String eventId;
    private String ticketType;
    private Double price;
    @Size(max = Validator.MAX_TICKET_QUANTITY)
    private Integer quantity;
    private Integer availableQuantity;
}
