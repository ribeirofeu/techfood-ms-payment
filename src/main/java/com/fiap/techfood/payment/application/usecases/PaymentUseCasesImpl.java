package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.PaymentValidation;
import com.fiap.techfood.payment.application.dto.request.CreatePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.interfaces.gateways.PaymentMessageSender;
import com.fiap.techfood.payment.application.interfaces.gateway.ExternalServicePayment;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.domain.commons.exception.PaymentAlreadyExistsException;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.Payment;
import com.fiap.techfood.payment.domain.payment.ReceivedPaymentStatusEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class PaymentUseCasesImpl implements PaymentUseCases {

    private final PaymentRepository repository;
    private final PaymentMessageSender paymentMessageSender;

    private final ExternalServicePayment externalServicePayment;

    public PaymentUseCasesImpl(PaymentRepository repository,
                               PaymentMessageSender paymentMessageSender,
                               ExternalServicePayment externalServicePayment) {
        this.repository = repository;
        this.paymentMessageSender = paymentMessageSender;
        this.externalServicePayment = externalServicePayment;
    }

    @Override
    public void createPayment(CreatePaymentDTO request) {
        Optional<Payment> paymentExists = repository.findById(request.getOrderId());

        if (paymentExists.isPresent()) {
            throw new PaymentAlreadyExistsException();
        }

        repository.save(Payment.builder()
                        .id(request.getOrderId())
                        .customerId(request.getCustomerId())
                        .totalValue(request.getTotalValue())
                        .createdDateTime(OffsetDateTime.now())
                        .status(PaymentStatus.CREATED)
                .build());
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
        payment.setStatus(PaymentStatus.WAITING_FOR_PAYMENT);

        repository.save(payment);

        return ProcessPaymentDTO.fromPayment(payment);
    }

    @Override
    public PaymentDTO processPayment(PaymentProcessedDTO request) {

        Payment payment = repository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("Pedido não encontrado.", HttpStatusCodes.BAD_REQUEST));

        validateReceivedStatus(request, payment);
        payment.setStatus(request.getStatus());
        repository.updatePaymentStatus(payment);

        paymentMessageSender.publish(ReceivedPaymentStatusEvent.builder()
                        .paymentDateTime(OffsetDateTime.now())
                        .paymentStatus(payment.getStatus())
                        .orderId(payment.getId())
                        .customerId(payment.getCustomerId())
                .build());

        return PaymentDTO.fromPayment(payment);
    }

    private void validateReceivedStatus(PaymentProcessedDTO request, Payment payment) {
        if (!List.of(PaymentStatus.APPROVED, PaymentStatus.REJECTED).contains(request.getStatus())) {
            throw new BusinessException(ErrorCodes.UNEXPECTED_STATUS.getMessage(), HttpStatusCodes.BAD_REQUEST);
        }

        if (!payment.getStatus().equals(PaymentStatus.WAITING_FOR_PAYMENT)) {
            throw new BusinessException(ErrorCodes.PAYMENT_ALREADY_PROCESSED.getMessage(), HttpStatusCodes.BAD_REQUEST);
        }
    }

    @Override
    public PaymentDTO getPayment(long orderId) {
        Payment payment = repository.findById(orderId).orElseThrow(
                () -> new BusinessException("Pedido não encontrado.", HttpStatusCodes.BAD_REQUEST)
        );

        return PaymentDTO.fromPayment(payment);
    }
}
