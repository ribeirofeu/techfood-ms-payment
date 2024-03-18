package com.fiap.techfood.payment.application.dto;

import com.fiap.techfood.payment.application.dto.request.CreatePaymentDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreatePaymentDTOTest {

    @Test
    public void testGetters() {
        // Arrange and Act
        CreatePaymentDTO paymentDTO = CreatePaymentDTO.builder()
                .orderId(100L)
                .customerId(200L)
                .totalValue(BigDecimal.valueOf(50.0))
                .build();

        // Assert
        assertThat(paymentDTO.getOrderId()).isEqualTo(100L);
        assertThat(paymentDTO.getCustomerId()).isEqualTo(200L);
        assertThat(paymentDTO.getTotalValue()).isEqualByComparingTo(BigDecimal.valueOf(50.0));
    }

    @Test
    public void testBuilder() {
        // Arrange and Act
        CreatePaymentDTO paymentDTO = CreatePaymentDTO.builder()
                .orderId(100L)
                .customerId(200L)
                .totalValue(BigDecimal.valueOf(50.0))
                .build();

        // Assert
        assertThat(paymentDTO.getOrderId()).isEqualTo(100L);
        assertThat(paymentDTO.getCustomerId()).isEqualTo(200L);
        assertThat(paymentDTO.getTotalValue()).isEqualByComparingTo(BigDecimal.valueOf(50.0));
    }
}
