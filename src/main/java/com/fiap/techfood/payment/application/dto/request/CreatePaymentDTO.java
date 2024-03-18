package com.fiap.techfood.payment.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreatePaymentDTO {
    private Long orderId;
    private Long customerId;
    private BigDecimal totalValue;
}
