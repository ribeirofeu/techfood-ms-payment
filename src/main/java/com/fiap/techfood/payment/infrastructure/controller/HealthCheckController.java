package com.fiap.techfood.payment.infrastructure.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckController implements HealthIndicator {

    public HealthCheckController() throws UnsupportedOperationException {}

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        return Health.up().build();
    }
}
