package com.fiap.techfood.payment.domain.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    SUCCESS(0, "Sucesso."),
    NULL_OR_INVALID_ORDER_NUMBER(1, "Id do pedido nulo ou inválido. Não são suportados valores negativos."),
    UNEXPECTED_STATUS(3, "Status de pagamento inesperado. Status esperado [APPROVED, REJECTED]"),
    PAYMENT_ALREADY_PROCESSED(4, "O pagamento já foi processado");

    private final int code;
    private final String message;
}
