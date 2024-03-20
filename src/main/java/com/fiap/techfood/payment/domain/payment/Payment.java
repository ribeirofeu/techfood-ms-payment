package com.fiap.techfood.payment.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {
    private long id;
    private Long customerId;
    private BigDecimal totalValue;
    private String qrCode;
    private OffsetDateTime createdDateTime;
    private PaymentStatus status;
}
