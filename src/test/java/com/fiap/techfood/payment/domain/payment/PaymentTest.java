package com.fiap.techfood.payment.domain.payment;

import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentTest {

    @Test
    void testPaymentGetterSetter() {
        // Arrange
        long id = 1L;
        BigDecimal totalValue = BigDecimal.valueOf(100.0);
        String qrCode = "QR_CODE";
        OffsetDateTime createdDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        PaymentStatus status = PaymentStatus.WAITING_FOR_PAYMENT;

        // Act
        Payment payment = Payment.builder()
                .id(id)
                .totalValue(totalValue)
                .qrCode(qrCode)
                .createdDateTime(createdDateTime)
                .status(status)
                .build();

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(totalValue, payment.getTotalValue());
        assertEquals(qrCode, payment.getQrCode());
        assertEquals(createdDateTime, payment.getCreatedDateTime());
        assertEquals(status, payment.getStatus());

        // Modify values
        long newId = 2L;
        BigDecimal newTotalValue = BigDecimal.valueOf(200.0);
        String newQrCode = "NEW_QR_CODE";
        OffsetDateTime newCreatedDateTime = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
        PaymentStatus newStatus = PaymentStatus.APPROVED;

        // Act: Set new values
        payment.setId(newId);
        payment.setTotalValue(newTotalValue);
        payment.setQrCode(newQrCode);
        payment.setCreatedDateTime(newCreatedDateTime);
        payment.setStatus(newStatus);

        // Assert: Verify new values
        assertEquals(newId, payment.getId());
        assertEquals(newTotalValue, payment.getTotalValue());
        assertEquals(newQrCode, payment.getQrCode());
        assertEquals(newCreatedDateTime, payment.getCreatedDateTime());
        assertEquals(newStatus, payment.getStatus());
    }

    @Test
    void testPaymentBuilder() {
        // Arrange
        long id = 1L;
        BigDecimal totalValue = BigDecimal.valueOf(100.0);

        // Act
        Payment payment = Payment.generate(id, totalValue);

        // Assert
        assertNotNull(payment);
        assertEquals(id, payment.getId());
        assertEquals(totalValue, payment.getTotalValue());
        assertNotNull(payment.getQrCode());
        assertNotNull(payment.getCreatedDateTime());
        assertEquals(PaymentStatus.WAITING_FOR_PAYMENT, payment.getStatus());
    }

}
