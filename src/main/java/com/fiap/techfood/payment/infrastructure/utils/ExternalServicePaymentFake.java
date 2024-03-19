package com.fiap.techfood.payment.infrastructure.utils;

import com.fiap.techfood.payment.application.interfaces.gateways.ExternalServicePayment;

import java.security.SecureRandom;

public class ExternalServicePaymentFake implements ExternalServicePayment {

    public ExternalServicePaymentFake() {}

    private static final int CODE_SIZE = 10;

    public String generateQRCode() {
        return Utils.convertToBase64(generatePaymentCode());
    }

    private static String generatePaymentCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_SIZE);

        for (int i = 0; i < CODE_SIZE; i++) {
            int digit = random.nextInt(CODE_SIZE);
            sb.append(digit);
        }

        return sb.toString();
    }
}
