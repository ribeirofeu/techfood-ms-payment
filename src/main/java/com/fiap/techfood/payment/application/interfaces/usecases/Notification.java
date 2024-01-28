package com.fiap.techfood.payment.application.interfaces.usecases;

import com.fiap.techfood.payment.application.dto.request.ProductionDTO;
import org.springframework.http.ResponseEntity;

public interface Notification {
    ResponseEntity<String> send(ProductionDTO request);
}
