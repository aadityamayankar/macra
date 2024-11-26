package com.mayankar.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RazorPayOrderDto {
    private String id;
    private Integer amount;
    private Integer amountPaid;
    private Integer amountDue;
    private String currency;
    private String receipt;
    private String status;
    private String entity;
    private Integer attempts;
}
