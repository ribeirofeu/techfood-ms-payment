package com.fiap.techfood.payment.domain.payment;

import com.fiap.techfood.payment.domain.commons.Event;
import com.fiap.techfood.payment.domain.commons.enums.EventType;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class ReceivedPaymentStatusEvent implements Event {
    private Long orderId;
    private Long customerId;
    private PaymentStatus paymentStatus;
    private OffsetDateTime paymentDateTime;

    @Override
    public EventType getEventType() {
        return EventType.RECEIVED_PAYMENT_STATUS;
    }
}
