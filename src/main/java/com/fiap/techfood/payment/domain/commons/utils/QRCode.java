package com.fiap.techfood.payment.domain.commons.utils;

import java.security.SecureRandom;

public class QRCode {

    private QRCode() {}

    private static final int CODE_SIZE = 10;

    public static String generateQRCode() {
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
