package com.mayankar.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table("user_ticket_assignment")
public class UserTicketAssignment extends BaseEntityProfile {
    @Id
    private Long id;
    private Long userId;
    private Long ticketId;
    private Long eventId;
    private Integer quantity;
}
