package com.fiap.techfood.payment.domain.payment;

import com.fiap.techfood.payment.domain.commons.enums.EventType;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ReceivedPaymentStatusEventTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange and Act
        ReceivedPaymentStatusEvent event = ReceivedPaymentStatusEvent.builder()
                .orderId(100L)
                .customerId(200L)
                .paymentStatus(PaymentStatus.WAITING_FOR_PAYMENT)
                .paymentDateTime(OffsetDateTime.now())
                .build();

        // Assert getters
        assertThat(event.getOrderId()).isEqualTo(100L);
        assertThat(event.getCustomerId()).isEqualTo(200L);
        assertThat(event.getPaymentStatus()).isEqualTo(PaymentStatus.WAITING_FOR_PAYMENT);
        assertThat(event.getPaymentDateTime()).isNotNull();

        // Assert  setters
        event.setOrderId(150L);
        event.setCustomerId(250L);
        event.setPaymentStatus(PaymentStatus.CREATED);
        event.setPaymentDateTime(OffsetDateTime.now().plusHours(1));

        assertThat(event.getOrderId()).isEqualTo(150L);
        assertThat(event.getCustomerId()).isEqualTo(250L);
        assertThat(event.getPaymentStatus()).isEqualTo(PaymentStatus.CREATED);
        assertThat(event.getPaymentDateTime()).isNotNull();
    }

    @Test
    public void testGetEventType() {
        // Arrange and Act
        ReceivedPaymentStatusEvent event = ReceivedPaymentStatusEvent.builder()
                .orderId(100L)
                .customerId(200L)
                .paymentStatus(PaymentStatus.CREATED)
                .paymentDateTime(OffsetDateTime.now())
                .build();

        // Assert
        assertThat(event.getEventType()).isEqualTo(EventType.RECEIVED_PAYMENT_STATUS);
    }
}
