package com.fiap.techfood.payment.infrastructure.utils;

import com.fiap.techfood.payment.application.interfaces.gateway.ExternalServicePayment;
import com.fiap.techfood.payment.infrastructure.utils.ExternalServicePaymentFake;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QRCodeTest {

    @Test
    void testGenerateQRCode() {
        // Act
        ExternalServicePayment externalServicePayment = new ExternalServicePaymentFake();
        String qrCode1 = externalServicePayment.generateQRCode();
        String qrCode2 = externalServicePayment.generateQRCode();

        // Assert
        assertNotNull(qrCode1);
        assertNotNull(qrCode2);
        assertNotEquals(qrCode1, qrCode2);
        assertTrue(qrCode1.matches("^[A-Za-z0-9+/]+={0,2}$"));
    }

}
