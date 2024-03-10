package com.fiap.techfood.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class GeneratePaymentDTO {

    @JsonProperty("idPedido")
    private Long orderId;

    @JsonProperty("valorTotal")
    private BigDecimal totalValue;
}
