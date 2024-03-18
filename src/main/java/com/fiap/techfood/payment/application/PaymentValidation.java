package com.fiap.techfood.payment.application;

import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;

public class PaymentValidation {

    private PaymentValidation() { }

    public static ErrorCodes generatePaymentDTO(GeneratePaymentDTO payment) {

        if (isInvalidOrderId(payment.getOrderId())) {
            return ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER;
        }

        return ErrorCodes.SUCCESS;
    }

    private static boolean isInvalidOrderId(Long id) {
        return id == null || id < 0;
    }
}
