package com.fiap.techfood.payment.domain.commons.exception;

import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int httpStatus;

    public BusinessException(String message, HttpStatusCodes code) {
        super(message);
        this.httpStatus = code.getCode();
    }

}
