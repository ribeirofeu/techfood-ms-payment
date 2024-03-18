package com.fiap.techfood.payment.domain.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.commons.utils.QRCode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {
    private long id;
    private Long customerId;
    private BigDecimal totalValue;
    private OffsetDateTime createdDateTime;
    private PaymentStatus status;

    public static Payment generate(long id, BigDecimal totalValue) {
        return Payment.builder()
                .id(id)
                .totalValue(totalValue)
                .createdDateTime(OffsetDateTime.now(ZoneOffset.UTC))
                .status(PaymentStatus.WAITING_FOR_PAYMENT)
                .build();
    }

}
