package com.fiap.techfood.payment.infrastructure.controller;

import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.dto.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private final PaymentUseCases service;

    @Tag(name = "Gera dados para o pagamento")
    @PostMapping
    public ResponseEntity<ProcessPaymentDTO> generatePayment(@RequestBody GeneratePaymentDTO request) {
        return ResponseEntity.ok(service.generatePaymentQRCode(request));
    }

    @Tag(name = "Processa pagamento")
    @PostMapping("/webhook")
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody ProcessPaymentDTO request) {
        return ResponseEntity.ok(service.processPayment(request));
    }
}
