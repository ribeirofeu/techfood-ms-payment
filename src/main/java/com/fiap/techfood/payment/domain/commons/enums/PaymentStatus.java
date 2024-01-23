package com.fiap.techfood.payment.domain.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    WAITING_FOR_PAYMENT(0),
    APPROVED(1),
    REJECTED(2);

    private final int status;
}

