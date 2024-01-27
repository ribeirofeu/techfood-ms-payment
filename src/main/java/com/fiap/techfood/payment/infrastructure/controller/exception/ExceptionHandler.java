package com.fiap.techfood.payment.infrastructure.controller.exception;

import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerBusinessException(BusinessException be) {
        return ResponseEntity.status(be.getHttpStatus()).body(be.getMessage());
    }

}
