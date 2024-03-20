package com.fiap.techfood.payment.infrastructure.messaging.listeners;

import com.fiap.techfood.payment.application.dto.request.CreatePaymentDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.domain.commons.exception.PaymentAlreadyExistsException;
import com.fiap.techfood.payment.infrastructure.messaging.events.CreatedOrderEvent;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@Profile("!test")
public class CreatedOrderListener {

    private PaymentUseCases paymentUseCases;

    @SqsListener(value = "${events.queues.created-order}")
    public void listenCreatedOrderEvent(CreatedOrderEvent event) {
        log.info("Mensagem Recebida. Order Id: {}", event.number());
        System.out.println(event);
        try {
            paymentUseCases.createPayment(CreatePaymentDTO.builder()
                    .orderId(event.number())
                    .totalValue(event.totalValue())
                    .customerId(event.customerId())
                    .build());
            log.info("Pagamento criado com sucesso!");
        } catch (PaymentAlreadyExistsException e) {
            log.info("Pagamento já criado");
        }
        catch (Exception e) {
            log.error("Erro ao processar a mensagem. Order id: {}", event.number());
            throw e;
        }
    }
}
