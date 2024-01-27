package com.fiap.techfood.payment.domain.utils;

import com.fiap.techfood.payment.domain.commons.utils.QRCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QRCodeTest {

    @Test
    void testGenerateQRCode() {
        // Act
        String qrCode1 = QRCode.generateQRCode();
        String qrCode2 = QRCode.generateQRCode();

        // Assert
        assertNotNull(qrCode1);
        assertNotNull(qrCode2);
        assertNotEquals(qrCode1, qrCode2);
        assertTrue(qrCode1.matches("^[A-Za-z0-9+/]+={0,2}$"));
    }

}
