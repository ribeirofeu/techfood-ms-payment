package com.fiap.techfood.payment.domain.interfaces.gateway;

import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.payment.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Transactional
public class PaymentRepositoryITest {

    @Autowired
    private PaymentRepository repository;

    @Test
    void givenValidPayment_whenSavePayment_thenShouldReturnSavedPayment() {
        //Arrange
        var payment = Payment.generate(10, BigDecimal.valueOf(10.5));

        //Act
        var paymentSaved = repository.save(payment);

        //Assert
        assertThat(paymentSaved).isNotNull().isEqualTo(payment);
    }

    @Test
    void givenExistingPaymentId_whenFindPaymentById_thenResultShouldContainCorrectPayment() {
        //Arrange
        //Act
        var optionalPayment = repository.findById(2);

        //Assert
        assertThat(optionalPayment).isNotNull().isPresent();
        optionalPayment.ifPresent(payment -> {
            assertThat(payment.getId()).isEqualTo(2);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.WAITING_FOR_PAYMENT);
        });
    }

    @Test
    void givenNonExistingPaymentId_whenFindPaymentById_thenResultShouldBeEmpty() {
        //Arrange
        //Act
        var paymentSaved = repository.findById(12);

        //Assert
        assertThat(paymentSaved).isNotPresent();
    }

}
