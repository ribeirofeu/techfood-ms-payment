package com.fiap.techfood.payment.domain.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpStatusCodes {
    NOT_FOUND(404),
    BAD_REQUEST(400),
    SERVICE_UNAVAILABLE(503);

    private final int code;
}