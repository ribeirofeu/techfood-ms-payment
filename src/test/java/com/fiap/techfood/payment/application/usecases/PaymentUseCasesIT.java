package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.interfaces.gateways.PaymentMessageSender;
import com.fiap.techfood.payment.application.interfaces.gateway.ExternalServicePayment;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.ReceivedPaymentStatusEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Transactional
class PaymentUseCasesIT {

    @Autowired
    private PaymentUseCases useCases;

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private ExternalServicePayment externalServicePayment;

    @Mock
    private PaymentMessageSender paymentMessageSender;

    @InjectMocks
    private PaymentUseCasesImpl mockUseCases;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        mockUseCases = new PaymentUseCasesImpl(repository, paymentMessageSender, externalServicePayment);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void givenValidPayment_thenShouldReturnOptionalOfPayment() {
        //Arrange
        var orderId = 1L;
        var generatePayment = new GeneratePaymentDTO(orderId);

        //Act
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.generatePaymentQRCode(generatePayment));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo("Pedido não encontrado.");
    }

    @Test
    void givenValidPaymentDTO_thenShouldReturnPaymentDTO() {
        //Arrange
        long orderId = 2L;

        //Act
        var paymentDTO = useCases.getPayment(orderId);

        //Assert
        assertThat(paymentDTO).isNotNull();
        assertThat(paymentDTO.toString()).isNotNull();
        assertThat(paymentDTO.getTotalValue()).isNotNull();
        assertThat(paymentDTO.getDetails()).isNotNull();
        assertThat(paymentDTO.getStatus()).isNotNull();
        assertThat(paymentDTO.getId()).isEqualTo(orderId);
    }

    @Test
    void getPaymentDTO_WhenOrderIdIsInvalid_ThrowsBusinessException() {
        //Arrange
        long orderId = 20L;

        //Act
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.getPayment(orderId));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo("Pedido não encontrado.");
    }

    @Test
    void generatePaymentQRCode_WhenOrderIdIsNull_ThrowsBusinessException() {
        //Arrange
        Long orderId = null;
        var totalValue = BigDecimal.valueOf(11.2);
        var generatePayment = new GeneratePaymentDTO(orderId);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.generatePaymentQRCode(generatePayment));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage());
    }

    @Test
    void generatePaymentQRCode_WhenInvalidOrderId_ThrowsBusinessException() {
        //Arrange
        Long orderId = -1L;
        var totalValue = BigDecimal.valueOf(11.2);
        var generatePayment = new GeneratePaymentDTO(orderId);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.generatePaymentQRCode(generatePayment));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage());
    }

    @Test
    void generatePaymentQRCode_WhenNotificationSucceeds_ReturnsSuccessfulPaymentDTO() {
        //Arrange
        var paymentProcessedDTO = new PaymentProcessedDTO(2L, PaymentStatus.APPROVED);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK);
        }).when(paymentMessageSender).publish(any(ReceivedPaymentStatusEvent.class));

        // Act
        var paymentDTO = mockUseCases.processPayment(paymentProcessedDTO);

        //Assert
        assertThat(paymentDTO).isNotNull();
        assertThat(paymentDTO.toString()).isNotNull();
        verify(paymentMessageSender, times(1)).publish(any(ReceivedPaymentStatusEvent.class));
    }

    @Test
    void processPayment_WhenStausUnexpected_ThrowsBusinessException() {
        //Arrange
        var paymentProcessedDTO = new PaymentProcessedDTO(2L, PaymentStatus.WAITING_FOR_PAYMENT);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.CREATED);
        }).when(paymentMessageSender).publish(any(ReceivedPaymentStatusEvent.class));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockUseCases.processPayment(paymentProcessedDTO));

        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.UNEXPECTED_STATUS.getMessage());
    }
}
