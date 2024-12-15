package com.mayankar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("order_profile")
public class OrderProfile extends BaseEntityProfile {
    @Id
    private Long id;
    private Long userId;
    private Long eventId;
    private Long ticketId;
    private Integer quantity;
    private Double totalAmount;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String paymentStatus;
}
