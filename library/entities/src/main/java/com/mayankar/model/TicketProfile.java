package com.mayankar.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("ticket_profile")
public class TicketProfile extends BaseEntityProfile {
    @Id
    private Long id;
    private Long eventId;
    private String ticketType;
    private Double price;
    private Integer quantity;
    private Integer availableQuantity;
}
