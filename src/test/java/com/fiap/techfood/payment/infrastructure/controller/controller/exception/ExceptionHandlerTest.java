package com.fiap.techfood.payment.infrastructure.controller.controller.exception;

import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.infrastructure.controller.exception.ExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionHandlerTest {

    @Test
    void testHandlerBusinessException() {
        // Arrange
        BusinessException businessException = new BusinessException("Test message", HttpStatusCodes.BAD_REQUEST);
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        // Act
        ResponseEntity responseEntity = exceptionHandler.handlerBusinessException(businessException);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatusCodes.BAD_REQUEST.getCode(), responseEntity.getStatusCode().value());
        assertEquals(businessException.getMessage(), responseEntity.getBody());
        assertEquals(businessException.getHttpStatus(), responseEntity.getStatusCode().value());
    }

}
