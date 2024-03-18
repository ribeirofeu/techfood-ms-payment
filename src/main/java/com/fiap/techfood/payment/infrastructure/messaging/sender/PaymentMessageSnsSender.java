package com.fiap.techfood.payment.infrastructure.messaging.sender;

import com.fiap.techfood.payment.application.interfaces.gateways.PaymentMessageSender;
import com.fiap.techfood.payment.domain.commons.Event;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PaymentMessageSnsSender implements PaymentMessageSender {
    @Value("${events.output}")
    private String topic;

    private final SnsTemplate snsTemplate;

    public PaymentMessageSnsSender(SnsTemplate snsTemplate) {
        this.snsTemplate = snsTemplate;
    }

    @Override
    public <T extends Event> void publish(T event) {
        snsTemplate.convertAndSend(topic, event);
        log.info("Message sent to the topic");
    }
}
