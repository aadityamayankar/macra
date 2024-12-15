package com.mayankar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProfileDto {
    private String userId;
    private String eventId;
    private String ticketId;
    private Integer quantity;
    private Double totalAmount;
    private String paymentStatus;
}
