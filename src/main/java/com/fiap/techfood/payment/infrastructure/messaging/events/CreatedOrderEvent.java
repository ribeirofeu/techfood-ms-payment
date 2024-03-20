package com.fiap.techfood.payment.infrastructure.messaging.events;

import com.fiap.techfood.payment.domain.commons.enums.EventType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


public record CreatedOrderEvent (
        Long number,
        Long customerId,
        BigDecimal totalValue,
        OffsetDateTime createdDateTime,
        String eventType) {

}
