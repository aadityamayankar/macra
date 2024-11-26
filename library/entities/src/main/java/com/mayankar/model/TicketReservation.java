package com.mayankar.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TicketReservation {
    @Builder.Default
    private String id = java.util.UUID.randomUUID().toString();
    private String userId;
    private String eventId;
    private String ticketId;
    private Integer reservedQuantity;
    private Double totalAmount;
    private Instant createdAt;
}
