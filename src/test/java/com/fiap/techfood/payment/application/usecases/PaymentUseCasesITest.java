package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.dto.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.ProductionDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.PaymentUseCases;
import com.fiap.techfood.payment.application.service.Notification;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.infrastructure.repository.PaymentRepositoryImpl;
import com.fiap.techfood.payment.infrastructure.repository.SpringPaymentRepository;
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
public class PaymentUseCasesITest {

    @Autowired
    private PaymentUseCases useCases;

    @Autowired
    private PaymentRepository repository;

    @Mock
    private Notification service;

    @InjectMocks
    private PaymentUseCasesImpl mockUseCases;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        mockUseCases = new PaymentUseCasesImpl(repository, service);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void givenValidPayment_thenShouldReturnOptionalOfPayment() {
        //Arrange
        var orderId = 10L;
        var totalValue = BigDecimal.valueOf(11.2);
        var generatePayment = new GeneratePaymentDTO(orderId, totalValue);
        //Act
        var optionalPayment = useCases.generatePaymentQRCode(generatePayment);

        //Assert
        assertThat(optionalPayment).isNotNull();
        assertThat(optionalPayment.getQrCode()).isNotNull();
        assertThat(optionalPayment.getId()).isEqualTo(orderId);
        assertThat(optionalPayment.getTotalValue()).isEqualTo(totalValue);
    }

    @Test
    void generatePaymentQRCode_WhenOrderIdIsNull_ThrowsBusinessException() {
        //Arrange
        Long orderId = null;
        var totalValue = BigDecimal.valueOf(11.2);
        var generatePayment = new GeneratePaymentDTO(orderId, totalValue);

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
        var generatePayment = new GeneratePaymentDTO(orderId, totalValue);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.generatePaymentQRCode(generatePayment));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage());
    }

    @Test
    void generatePaymentQRCode_WhenTotalValueIsNull_ThrowsBusinessException() {
        //Arrange
        Long orderId = 10L;
        BigDecimal totalValue = null;
        var generatePayment = new GeneratePaymentDTO(orderId, totalValue);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.generatePaymentQRCode(generatePayment));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
    }

    @Test
    void generatePaymentQRCode_WhenInvalidTotalValue_ThrowsBusinessException() {
        //Arrange
        Long orderId = 10L;
        BigDecimal totalValue = BigDecimal.valueOf(-11.2);
        var generatePayment = new GeneratePaymentDTO(orderId, totalValue);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.generatePaymentQRCode(generatePayment));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
    }

    @Test
    void generatePaymentQRCode_WhenNotificationSucceeds_ReturnsSuccessfulPaymentDTO() {
        //Arrange
        var payment = repository.findById(2L).orElseThrow();
        var processPaymentDto = ProcessPaymentDTO.fromPayment(payment);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.CREATED);
        }).when(service).send(any(ProductionDTO.class));

        // Act
        var paymentDTO = mockUseCases.processPayment(processPaymentDto);

        //Assert
        assertThat(paymentDTO).isNotNull();
        verify(service, times(1)).send(any(ProductionDTO.class));
    }

    @Test
    void processPayment_WhenNotificationFailsWithIncorrectStatusCode_ThrowsBusinessException() {
        //Arrange
        var payment = repository.findById(2L).orElseThrow();
        var processPaymentDto = ProcessPaymentDTO.fromPayment(payment);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK);
        }).when(service).send(any(ProductionDTO.class));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockUseCases.processPayment(processPaymentDto));
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.SERVICE_UNAVAILABLE.getCode());
        assertThat(exception.getMessage()).isEqualTo("Falha ao enviar pedido para produção. O serviço externo retornou um status inesperado: 200");
    }

    @Test
    void processPayment_WhenNotificationServiceFails_ThrowsBusinessException() {
        //Arrange
        var payment = repository.findById(2L).orElseThrow();
        var processPaymentDto = ProcessPaymentDTO.fromPayment(payment);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.processPayment(processPaymentDto));

        //Asset
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.SERVICE_UNAVAILABLE.getCode());
        assertThat(exception.getMessage()).isEqualTo("Falha ao enviar pedido para produção. Tente novamente mais tarde! :(");
    }

    @Test
    void processPayment_WhenPaymentNotFound_ThrowsBusinessException() {
        //Arrange
        var processPaymentDto = ProcessPaymentDTO.builder().id(10L).build();

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCases.processPayment(processPaymentDto));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo("Pedido não encontrado.");
    }

    @Test
    void processPayment_WhenInvalidQRCode_ThrowsBusinessException() {
        //Arrange
        var payment = repository.findById(2L).orElseThrow();
        payment.setQrCode("invalid");
        var processPaymentDto = ProcessPaymentDTO.fromPayment(payment);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK);
        }).when(service).send(any(ProductionDTO.class));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockUseCases.processPayment(processPaymentDto));
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.INVALID_QRCODE.getMessage());
    }

    @Test
    void processPayment_WhenEmptyQRCode_ThrowsBusinessException() {
        //Arrange
        var payment = repository.findById(2L).orElseThrow();
        payment.setQrCode("");
        var processPaymentDto = ProcessPaymentDTO.fromPayment(payment);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK);
        }).when(service).send(any(ProductionDTO.class));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockUseCases.processPayment(processPaymentDto));
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_QRCODE.getMessage());
    }

    @Test
    void processPayment_WhenInvalidTotalValue_ThrowsBusinessException() {
        //Arrange
        var payment = repository.findById(2L).orElseThrow();
        payment.setTotalValue(BigDecimal.valueOf(-2));
        var processPaymentDto = ProcessPaymentDTO.fromPayment(payment);

        doAnswer(invocation -> {
            return new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK);
        }).when(service).send(any(ProductionDTO.class));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockUseCases.processPayment(processPaymentDto));
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
    }
}
