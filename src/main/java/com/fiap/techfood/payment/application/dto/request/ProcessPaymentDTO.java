package com.fiap.techfood.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fiap.techfood.payment.domain.payment.Payment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProcessPaymentDTO {

    @JsonProperty("idPedido")
    private Long id;

    @JsonProperty("qrCode")
    private String qrCode;

    @JsonProperty("valorTotal")
    private BigDecimal totalValue;

    public static ProcessPaymentDTO fromPayment(Payment payment) {
        return ProcessPaymentDTO.builder()
                .id(payment.getId())
                .totalValue(payment.getTotalValue())
                .build();
    }
}
