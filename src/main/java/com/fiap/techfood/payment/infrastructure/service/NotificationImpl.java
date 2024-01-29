package com.fiap.techfood.payment.infrastructure.service;

import com.fiap.techfood.payment.application.dto.request.ProductionDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class NotificationImpl implements Notification {

    private final RestTemplate restTemplate;

    @Value("${production.url}")
    private String productionUrl;

    public NotificationImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> send(ProductionDTO request) {
        HttpEntity<ProductionDTO> requestEntity = new HttpEntity<>(request);

        return restTemplate.postForEntity(
                productionUrl,
                requestEntity,
                String.class
        );
    }
}
