package com.fiap.techfood.payment.infrastructure.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.dto.response.PaymentDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.infrastructure.controller.PaymentController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentUseCases service;

    @Mock
    private PaymentRepository repository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        PaymentController controller = new PaymentController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

//    @Test
//    void generatePaymentWithErrorRepositoryConnectionRepository() throws Exception {
//        //Arrange
//        var generatePaymentDTO = new GeneratePaymentDTO(1L);
//
//        var processPaymentDTO = ProcessPaymentDTO.builder()
//                .id(1L)
//                .totalValue(BigDecimal.valueOf(10.5))
//                .qrCode("")
//                .build();
//
//        when(service.generatePaymentQRCode(any(GeneratePaymentDTO.class))).thenReturn(processPaymentDTO);
//
//        //Act
//        mockMvc.perform(
//                post("/payment/generate")
//                        .content(asJsonString(generatePaymentDTO))
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isBadRequest());
//
//        //Assert
//        verify(service, times(1)).generatePaymentQRCode(any(GeneratePaymentDTO.class));
//    }

    @Test
    void generatePaymentWithInvalidMediaType() throws Exception {
        //Arrange
        var generatePaymentDTO = "<GeneratePaymentDTO><idPedido>123</idPedido><valorTotal>10.5</valorTotal></GeneratePaymentDTO>";

        //Act
        mockMvc.perform(
                post("/payment/generate")
                        .content(asJsonString(generatePaymentDTO))
                        .contentType(MediaType.APPLICATION_XML)
        ).andExpect(status().isUnsupportedMediaType());

        //Assert
        verify(service, never()).generatePaymentQRCode(any(GeneratePaymentDTO.class));
    }

    @Test
    void processPaymentWithSuccess() throws Exception {
        //Arrange
        var processPaymentDTO = PaymentProcessedDTO.builder()
                .id(1L)
                .status(PaymentStatus.APPROVED)
                .build();

        var paymentDTO = PaymentDTO.builder().status(PaymentStatus.APPROVED).totalValue(BigDecimal.valueOf(10.5)).id(1L).details("TESTE").build();
        when(service.processPayment(any(PaymentProcessedDTO.class))).thenReturn(paymentDTO);

        //Act
        mockMvc.perform(
                post("/payment/webhook")
                        .content(asJsonString(processPaymentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        //Assert
        verify(service, times(1)).processPayment(any(PaymentProcessedDTO.class));
    }

    @Test
    void processPaymentWithInvalidMediaType() throws Exception {
        //Arrange
        var processPaymentDTO = "<ProcessPaymentDTO><idPedido>123</idPedido><status>\"APPROVED\"</status></ProcessPaymentDTO>";

        //Act
        mockMvc.perform(
                post("/payment/webhook")
                        .content(asJsonString(processPaymentDTO))
                        .contentType(MediaType.APPLICATION_XML)
        ).andExpect(status().isUnsupportedMediaType());

        //Assert
        verify(service, never()).processPayment(any(PaymentProcessedDTO.class));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}
