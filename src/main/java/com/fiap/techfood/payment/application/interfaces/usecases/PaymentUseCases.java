package com.fiap.techfood.payment.application.interfaces.usecases;

import com.fiap.techfood.payment.application.dto.request.CreatePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;

public interface PaymentUseCases {
    void createPayment(CreatePaymentDTO request);

    ProcessPaymentDTO generatePaymentQRCode(GeneratePaymentDTO request);

    PaymentDTO processPayment(PaymentProcessedDTO request);

    PaymentDTO getPayment(long orderId);
}
