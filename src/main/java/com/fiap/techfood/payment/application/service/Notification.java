package com.fiap.techfood.payment.application.service;

import com.fiap.techfood.payment.application.dto.request.ProductionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class Notification {

    private final RestTemplate restTemplate;

    @Value("${production.url}")
    private String productionUrl;

    public Notification(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> send(ProductionDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-cache");
        headers.set("Pragma", "no-cache");

        HttpEntity<ProductionDTO> requestEntity = new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(
                productionUrl,
                requestEntity,
                String.class
        );
    }
}
