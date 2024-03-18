package com.fiap.techfood.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePaymentDTO {

    @JsonProperty("idPedido")
    private Long orderId;
}
