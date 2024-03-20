package com.fiap.techfood.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PaymentProcessedDTO {
    @JsonProperty("idPedido")
    private Long id;

    @JsonProperty("status")
    private PaymentStatus status;
}
