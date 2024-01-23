package com.fiap.techfood.payment.application;

import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.ProcessPaymentDTO;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.payment.Payment;

import java.math.BigDecimal;

public class PaymentValidation {

    public static ErrorCodes generatePaymentDTO(GeneratePaymentDTO payment) {

        if (isInvalidOrderId(payment.getOrderId())) {
            return ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER;
        }

        if (isInvalidTotalValue(payment.getTotalValue())) {
            return ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE;
        }

        return ErrorCodes.SUCCESS;
    }

    public static ErrorCodes processPaymentDTO(ProcessPaymentDTO processPaymentDTO, Payment payment) {

        if (isInvalidOrderId(processPaymentDTO.getId())) {
            return ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER;
        }

        if (isInvalidTotalValue(processPaymentDTO.getTotalValue())) {
            return ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE;
        }

        if (isInvalidQRCode(processPaymentDTO.getQrCode())) {
            return ErrorCodes.NULL_OR_INVALID_QRCODE;
        }

        if (!processPaymentDTO.getQrCode().equals(payment.getQrCode())) {
            return ErrorCodes.INVALID_QRCODE;
        }

        return ErrorCodes.SUCCESS;
    }

    private static boolean isInvalidOrderId(Long id) {
        return id == null || id < 0;
    }

    private static boolean isInvalidTotalValue(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) < 0;
    }

    private static boolean isInvalidQRCode(String qrCode) {
        return qrCode == null || qrCode.isEmpty();
    }

}
