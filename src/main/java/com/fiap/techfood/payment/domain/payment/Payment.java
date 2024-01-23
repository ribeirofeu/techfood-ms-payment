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
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {
    private long id;
    private BigDecimal totalValue;
    private String qrCode;
    private OffsetDateTime createdDateTime;
    private PaymentStatus status;

    public static Payment generate(long id, BigDecimal totalValue) {
        return Payment.builder()
                .id(id)
                .totalValue(totalValue)
                .qrCode(QRCode.generateQRCode())
                .createdDateTime(OffsetDateTime.now(ZoneOffset.UTC))
                .status(PaymentStatus.WAITING_FOR_PAYMENT)
                .build();
    }

}
