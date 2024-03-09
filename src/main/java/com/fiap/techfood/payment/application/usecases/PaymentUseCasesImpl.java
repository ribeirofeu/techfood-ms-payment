package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.PaymentValidation;
import com.fiap.techfood.payment.application.dto.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.ProductionDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.Notification;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.infrastructure.service.NotificationImpl;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PaymentUseCasesImpl implements PaymentUseCases {

    private final PaymentRepository repository;
    private final Notification notification;

    public PaymentUseCasesImpl(PaymentRepository repository, Notification notification) {
        this.repository = repository;
        this.notification = notification;
    }

    @Override
    public ProcessPaymentDTO generatePaymentQRCode(GeneratePaymentDTO request) {

        ErrorCodes error = PaymentValidation.generatePaymentDTO(request);

        if (error != ErrorCodes.SUCCESS) {
            throw new BusinessException(error.getMessage(), HttpStatusCodes.BAD_REQUEST);
        }

        Payment payment = Payment.generate(request.getOrderId(), request.getTotalValue());

        repository.save(payment);

        return ProcessPaymentDTO.fromPayment(payment);
    }

    @Override
    public PaymentDTO processPayment(ProcessPaymentDTO request) {

        Payment payment = repository.findById(request.getId()).orElseThrow(
                () -> new BusinessException("Pedido não encontrado.", HttpStatusCodes.BAD_REQUEST)
        );

        ErrorCodes error = PaymentValidation.processPaymentDTO(request, payment);

        if (error != ErrorCodes.SUCCESS) {
            throw new BusinessException(error.getMessage(), HttpStatusCodes.BAD_REQUEST);
        }

        ResponseEntity<String> responseEntity;

        try {
            responseEntity = notification.send(new ProductionDTO(payment.getId()));

        } catch (Exception e) {
            throw new BusinessException("Falha ao enviar pedido para produção. Tente novamente mais tarde! :(",
                    HttpStatusCodes.SERVICE_UNAVAILABLE);
        }

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new BusinessException("Falha ao enviar pedido para produção. O serviço externo retornou um status inesperado: "
                    + responseEntity.getStatusCode().value(),
                    HttpStatusCodes.SERVICE_UNAVAILABLE);
        }

        payment.setStatus(PaymentStatus.APPROVED);

        repository.updatePaymentStatus(payment);

        return PaymentDTO.fromPayment(payment);
    }

    @Override
    public PaymentDTO getPayment(long orderId) {
        Payment payment = repository.findById(orderId).orElseThrow(
                () -> new BusinessException("Pedido não encontrado.", HttpStatusCodes.BAD_REQUEST)
        );

        return PaymentDTO.fromPayment(payment);
    }
}
