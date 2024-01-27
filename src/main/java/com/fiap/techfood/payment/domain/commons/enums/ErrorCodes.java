package com.fiap.techfood.payment.domain.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    SUCCESS(0, "Sucesso."),
    NULL_OR_INVALID_ORDER_NUMBER(1, "Id do pedido nulo ou inválido. Não são suportados valores negativos."),
    NULL_OR_INVALID_TOTAL_VALUE(2, "Valor total nulo ou inválido. Não são suportados valores negativos."),
    NULL_OR_INVALID_QRCODE(3, "QRCode nulo ou vazio."),
    INVALID_QRCODE(4, "QR Code inválido ou desatualizado");

    private final int code;
    private final String message;
}
