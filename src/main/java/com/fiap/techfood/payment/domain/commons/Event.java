package com.fiap.techfood.payment.domain.commons;

import com.fiap.techfood.payment.domain.commons.enums.EventType;

public interface Event {
    EventType getEventType();
}
