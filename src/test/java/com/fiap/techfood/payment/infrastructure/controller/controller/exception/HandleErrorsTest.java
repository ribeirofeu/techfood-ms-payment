package com.fiap.techfood.payment.infrastructure.controller.controller.exception;

import com.fiap.techfood.payment.infrastructure.controller.exception.HandleErrors;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HandleErrorsTest {

    @Test
    void testHandlerError400() {
        // Arrange
        String errorMessage = "Test error message";
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(errorMessage);
        HandleErrors handleErrors = new HandleErrors();

        // Act
        ResponseEntity responseEntity = handleErrors.handlerError400(exception);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }

}
