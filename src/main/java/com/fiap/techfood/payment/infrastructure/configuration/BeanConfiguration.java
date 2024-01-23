package com.fiap.techfood.payment.infrastructure.configuration;

import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.application.service.Notification;
import com.fiap.techfood.payment.application.usecases.PaymentUseCasesImpl;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class BeanConfiguration {

    @Bean
    PaymentUseCases paymentUseCases(PaymentRepository repository, Notification notification) {
        return new PaymentUseCasesImpl(repository, notification);
    }

    @Bean
    Notification notification(RestTemplate restTemplate) {
        return new Notification(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().setCacheControl(CacheControl.noCache());
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}
