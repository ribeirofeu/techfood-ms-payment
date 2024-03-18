package com.fiap.techfood.payment.infrastructure.controller;

import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentUseCases service;

    public PaymentController(PaymentUseCases service) {
        this.service = service;
    }

    @Tag(name = "Gera dados para o pagamento")
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProcessPaymentDTO> generatePayment(@RequestBody GeneratePaymentDTO request) {
        return ResponseEntity.ok(service.generatePaymentQRCode(request));
    }

    @Tag(name = "Processa pagamento")
    @PostMapping(value = "/webhook", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentProcessedDTO request) {
        return ResponseEntity.ok(service.processPayment(request));
    }

    @Tag(name = "Obter inforções de pagamento")
    @GetMapping(value = "/")
    public ResponseEntity<PaymentDTO> getPayment(@RequestBody GeneratePaymentDTO request) {
        return ResponseEntity.ok(service.getPayment(request.getOrderId()));
    }
}
