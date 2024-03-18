package com.fiap.techfood.payment.infrastructure.messaging;

import com.fiap.techfood.payment.domain.order.CreatedOrderEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SqsListerner {
    @SqsListener(value = "${messaging.input}")
    public void receiveCreatedOrder(CreatedOrderEvent createdOrderEvent) {

        System.out.println("Evento recebido");
        System.out.println(createdOrderEvent);

    }
}
