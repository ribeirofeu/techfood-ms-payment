package com.fiap.techfood.payment.infrastructure.controller.controller;

import com.fiap.techfood.payment.infrastructure.controller.HealthCheckController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthCheckControllerTest {

    @Test
    void healthCheckShouldReturnUpStatus() {
        // Arrange
        HealthCheckController healthCheckController = new HealthCheckController();

        // Act
        Status status = healthCheckController.health().getStatus();

        // Assert
        assertEquals(Status.UP, status);
    }

}
