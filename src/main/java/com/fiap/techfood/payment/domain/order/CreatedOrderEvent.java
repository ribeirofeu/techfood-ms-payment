package com.fiap.techfood.payment.domain.order;

import com.fiap.techfood.payment.domain.commons.Event;
import com.fiap.techfood.payment.domain.commons.enums.EventType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class CreatedOrderEvent implements Event {
    private Long number;
    private Long customerId;
    private BigDecimal totalValue;
    private OffsetDateTime createdDateTime;

    @Override
    public EventType getEventType() {
        return EventType.CREATED_ORDER;
    }
}
