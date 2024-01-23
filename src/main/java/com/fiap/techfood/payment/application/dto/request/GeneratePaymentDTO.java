package com.fiap.techfood.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GeneratePaymentDTO {

    @JsonProperty("idPedido")
    private Long orderId;

    @JsonProperty("valorTotal")
    private BigDecimal totalValue;
}
