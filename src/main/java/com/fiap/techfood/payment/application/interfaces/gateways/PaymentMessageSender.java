package com.fiap.techfood.payment.application.interfaces.gateways;


import com.fiap.techfood.payment.domain.commons.Event;

public interface PaymentMessageSender {
    <T extends Event> void publish(T event);
}
