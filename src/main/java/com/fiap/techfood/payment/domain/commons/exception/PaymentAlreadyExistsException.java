package com.fiap.techfood.payment.domain.commons.exception;

import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;

public class PaymentAlreadyExistsException extends BusinessException{
    public PaymentAlreadyExistsException() {
        super("Payment with the specified details already exists", HttpStatusCodes.BAD_REQUEST);
    }
}
