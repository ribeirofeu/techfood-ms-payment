package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.PaymentValidation;
import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.dto.request.ProductionDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.interfaces.gateway.ExternalServicePayment;
import com.fiap.techfood.payment.application.interfaces.usecases.Notification;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PaymentUseCasesImpl implements PaymentUseCases {

    private final PaymentRepository repository;
    private final Notification notification;

    private final ExternalServicePayment externalServicePayment;

    public PaymentUseCasesImpl(PaymentRepository repository, Notification notification, ExternalServicePayment externalServicePayment) {
        this.repository = repository;
        this.notification = notification;
        this.externalServicePayment = externalServicePayment;
    }

    @Override
    public ProcessPaymentDTO generatePaymentQRCode(GeneratePaymentDTO request) {

        ErrorCodes errorCodes = PaymentValidation.generatePaymentDTO(request);
        if (errorCodes != ErrorCodes.SUCCESS)
            throw new BusinessException(errorCodes.getMessage(), HttpStatusCodes.BAD_REQUEST);

        Payment payment = repository.findById(request.getOrderId()).orElseThrow(
                () -> new BusinessException("Pedido não encontrado.", HttpStatusCodes.BAD_REQUEST)
        );

        payment.setQrCode(externalServicePayment.generateQRCode());

        repository.save(payment);

        return ProcessPaymentDTO.fromPayment(payment);
    }

    @Override
    public PaymentDTO processPayment(PaymentProcessedDTO request) {

        Payment payment = repository.findById(request.getId()).orElseThrow(
                () -> new BusinessException("Pedido não encontrado.", HttpStatusCodes.BAD_REQUEST)
        );

        ErrorCodes error = PaymentValidation.processPaymentDTO(request);

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

        payment.setStatus(request.getStatus());

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
