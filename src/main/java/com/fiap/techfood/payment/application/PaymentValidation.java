package com.fiap.techfood.payment.application;

import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;

import java.math.BigDecimal;

public class PaymentValidation {

    private PaymentValidation() { }

    public static ErrorCodes generatePaymentDTO(GeneratePaymentDTO payment) {

        if (isInvalidOrderId(payment.getOrderId())) {
            return ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER;
        }

        if (isInvalidTotalValue(payment.getTotalValue())) {
            return ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE;
        }

        return ErrorCodes.SUCCESS;
    }

    public static ErrorCodes processPaymentDTO(PaymentProcessedDTO paymentProcessedDTO) {
        if (paymentProcessedDTO.getStatus() == PaymentStatus.WAITING_FOR_PAYMENT) {
            return ErrorCodes.UNEXPECTED_STATUS;
        }

        return ErrorCodes.SUCCESS;
    }

    private static boolean isInvalidOrderId(Long id) {
        return id == null || id < 0;
    }

    private static boolean isInvalidTotalValue(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) < 0;
    }
}
