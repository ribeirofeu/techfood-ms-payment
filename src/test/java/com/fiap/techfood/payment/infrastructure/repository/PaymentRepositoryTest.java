package com.fiap.techfood.payment.infrastructure.repository;

import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.Payment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Mock
    private PaymentRepository repository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void givenValidPayment_whenFindPaymentById_thenShouldReturnPayment() {
        //Arrange
        var payment = generatePayment();

        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(payment));

        //Act
        var optionalPayment = repository.findById(1);

        //Assert
        assertThat(optionalPayment).isNotNull().isNotEmpty().isEqualTo(Optional.ofNullable(payment));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void givenInvalidPaymentId_whenFindPaymentById_thenShouldReturnEmptyOptional() {
        //Arrange
        var payment = generatePayment();

        when(repository.findById(1)).thenReturn(Optional.ofNullable(payment));

        //Act
        var optionalPayment = repository.findById(2);

        //Assert
        assertThat(optionalPayment).isEmpty().isNotPresent();
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void givenValidPayment_whenSavePayment_thenShouldReturnSavedPayment() {
        //Arrange
        var payment = generatePayment();

        when(repository.save(any())).thenReturn(payment);

        //Act
        var paymentGenerated = repository.save(payment);

        //Assert
        assertThat(paymentGenerated).isNotNull().isEqualTo(payment);
        verify(repository, times(1)).save(any());
    }

    @Test
    void givenNullPayment_whenSavePayment_thenShouldReturnNull() {
        //Arrange
        var payment = generatePayment();

        when(repository.save(payment)).thenReturn(payment);

        //Act
        var paymentGenerated = repository.save(null);

        //Assert
        assertThat(paymentGenerated).isNull();
        verify(repository, times(1)).save(any());
    }

    @Test
    void givenPayment_whenUpdatePaymentStatus_thenShouldUpdateStatusToApproved() {
        //Arrange
        var payment = generatePayment();

        doAnswer(invocation -> {
            payment.setStatus(PaymentStatus.APPROVED);
            return null;
        }).when(repository).updatePaymentStatus(any());

        //Act
        repository.updatePaymentStatus(payment);

        //Assert
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        verify(repository, times(1)).updatePaymentStatus(payment);
    }

    private Payment generatePayment() {
        return Payment.builder()
                .id(1L)
                .totalValue(BigDecimal.valueOf(10.0))
                .customerId(1L)
                .qrCode("any")
                .status(PaymentStatus.WAITING_FOR_PAYMENT)
                .createdDateTime(OffsetDateTime.now())
                .build();
    }
}
