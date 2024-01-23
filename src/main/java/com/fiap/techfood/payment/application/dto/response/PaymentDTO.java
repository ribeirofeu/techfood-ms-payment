package com.fiap.techfood.payment.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.payment.Payment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentDTO {

    @JsonProperty("idPedido")
    private long id;

    @JsonProperty("valorTotal")
    private BigDecimal totalValue;

    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("detalhes")
    private String details;

    public static PaymentDTO fromPayment(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .totalValue(payment.getTotalValue())
                .status(payment.getStatus())
                .details(String.format("Pedido %s em %s.", payment.getStatus(), payment.getCreatedDateTime()))
                .build();
    }

}
