package com.fiap.techfood.payment.infrastructure.repository;

import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
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
        assertThat(paymentSaved).isNotNull();
        assertThat(paymentSaved.getId()).isEqualTo(payment.getId());
        assertThat(paymentSaved.getStatus()).isEqualTo(payment.getStatus());
        assertThat(paymentSaved.getTotalValue()).isEqualTo(payment.getTotalValue());
        assertThat(paymentSaved.getCreatedDateTime()).isEqualTo(payment.getCreatedDateTime());
        assertThat(paymentSaved.getQrCode()).isEqualTo(payment.getQrCode());
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

    @Test
    void givenPaymentWithStatusUpdated_whenChangeStatus_thenResultShouldContainCorrectStatus() {
        //Arrange
        var payment = repository.findById(2).orElseThrow();
        var oldStatus = payment.getStatus();
        var id = payment.getId();

        payment.setStatus(PaymentStatus.APPROVED);

        //Act
        repository.updatePaymentStatus(payment);

        //Assert
        assertThat(oldStatus).isNotEqualTo(payment.getStatus());
        assertThat(id).isEqualTo(payment.getId());
    }

    @Test
    void givenUpdatedPaymentStatus_whenStatusChanged_thenPaymentShouldMaintainOtherInformationUnchanged() {
        //Arrange
        var payment = repository.findById(2).orElseThrow();
        var oldStatus = payment.getStatus();
        var id = payment.getId();
        var qrCode = payment.getQrCode();
        var totalValue = payment.getTotalValue();

        payment.setStatus(PaymentStatus.REJECTED);

        //Act
        repository.updatePaymentStatus(payment);

        //Arrange
        assertThat(oldStatus).isNotEqualTo(payment.getStatus());
        assertThat(id).isEqualTo(payment.getId());
        assertThat(qrCode).isEqualTo(payment.getQrCode());
        assertThat(totalValue).isEqualTo(payment.getTotalValue());
        assertThat(payment.toString()).isNotEmpty();
    }

}
