package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.application.dto.request.PaymentProcessedDTO;
import com.fiap.techfood.payment.application.interfaces.usecases.Notification;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
import com.fiap.techfood.payment.domain.commons.enums.PaymentStatus;
import com.fiap.techfood.payment.domain.commons.exception.BusinessException;
import com.fiap.techfood.payment.domain.interfaces.gateway.PaymentRepository;
import com.fiap.techfood.payment.domain.payment.Payment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class PaymentUseCasesTest {

    @Mock
    private PaymentRepository mockRepository;

    @Mock
    private Notification mockNotification;

    private PaymentUseCasesImpl mockPaymentUseCases;

    AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        mockPaymentUseCases = new PaymentUseCasesImpl(mockRepository, mockNotification);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void generatePaymentQRCode_ValidRequest_Success() {
        //Arrange
        var generateQRCode = generatePaymentDTO();

        when(mockRepository.save(any())).thenReturn(generatePayment());

        //Act
        var processPaymentDto = mockPaymentUseCases.generatePaymentQRCode(generateQRCode);

        //Assert
        assertThat(processPaymentDto).isNotNull();
        assertThat(processPaymentDto.toString()).isNotNull();
        assertThat(processPaymentDto.getId()).isEqualTo(generateQRCode.getOrderId());
        assertThat(processPaymentDto.getTotalValue()).isEqualTo(generateQRCode.getTotalValue());
        assertThat(processPaymentDto.getQrCode()).isNotEmpty();
        verify(mockRepository, times(1)).save(any());
    }

    @Test
    void generatePaymentQRCode_InvalidRequest_WithNegativeId_ThrowsBusinessException() {
        //Arrange
        var generateQRCode = new GeneratePaymentDTO(-1L, BigDecimal.valueOf(10));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.generatePaymentQRCode(generateQRCode));

        //Assert
        verify(mockRepository, times(0)).save(any());
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage());
    }

    @Test
    void generatePaymentQRCode_InvalidRequest_WithNullId_ThrowsBusinessException() {
        //Arrange
        var generateQRCode = new GeneratePaymentDTO(null, BigDecimal.valueOf(10));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.generatePaymentQRCode(generateQRCode));

        //Assert
        verify(mockRepository, times(0)).save(any());
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage());
    }

    @Test
    void generatePaymentQRCode_InvalidRequest_WithInvalidTotalValue_ThrowsBusinessException() {
        //Arrange
        var generateQRCode =new GeneratePaymentDTO(1L, BigDecimal.valueOf(-10));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.generatePaymentQRCode(generateQRCode));

        //Assert
        verify(mockRepository, times(0)).save(any());
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
    }

    @Test
    void generatePaymentQRCode_InvalidRequest_WithNullTotalValue_ThrowsBusinessException() {
        //Arrange
        var generateQRCode = new GeneratePaymentDTO(1L, null);

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.generatePaymentQRCode(generateQRCode));

        //Assert
        verify(mockRepository, times(0)).save(any());
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
    }

    @Test
    void processPayment_ValidRequest_Success() {
        //Arrange
        var paymentProcessedDTO = new PaymentProcessedDTO(2L, PaymentStatus.APPROVED);

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));
        when(mockNotification.send(any()))
                .thenReturn(new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK));
        doNothing().when(mockRepository).updatePaymentStatus(any());

        //Act
        var paymentDTO = mockPaymentUseCases.processPayment(paymentProcessedDTO);

        //Assert
        assertThat(paymentDTO).isNotNull();
        assertThat(paymentDTO.toString()).isNotNull();
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(1)).updatePaymentStatus(any());
        verify(mockNotification, times(1)).send(any());
    }

    @Test
    void processPayment_NotFoundProcessPaymentId_ThrowsBusinessException() {
        //Arrange
        var paymentProcessedDTO = new PaymentProcessedDTO(20L, PaymentStatus.APPROVED);

        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(paymentProcessedDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo("Pedido não encontrado.");
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidNotification_ServiceUnavailable_ThrowsBusinessException() {
        //Arrange
        var paymentProcessedDTO = new PaymentProcessedDTO(1L, PaymentStatus.APPROVED);

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));
        when(mockNotification.send(any())).thenThrow(new RestClientException("Serviço indisponivel ou URI inválida"));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(paymentProcessedDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.SERVICE_UNAVAILABLE.getCode());
        assertThat(exception.getMessage())
                .isEqualTo("Falha ao enviar pedido para produção. Tente novamente mais tarde! :(");
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockNotification, times(1)).send(any());
    }

    @Test
    void givenValidPaymentDTO_thenShouldReturnPaymentDTO() {
        //Arrange
        long orderId = 1L;

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));

        //Act
        var paymentDTO = mockPaymentUseCases.getPayment(orderId);

        //Assert
        assertThat(paymentDTO).isNotNull();
        assertThat(paymentDTO.toString()).isNotNull();
        assertThat(paymentDTO.getId()).isEqualTo(1L);
        assertThat(paymentDTO.getDetails()).isNotNull();
        assertThat(paymentDTO.getTotalValue()).isNotNull();
        assertThat(paymentDTO.getStatus()).isEqualTo(PaymentStatus.WAITING_FOR_PAYMENT);
        verify(mockRepository, times(1)).findById(anyLong());
    }

    @Test
    void getPaymentDTO_WhenOrderIdIsInvalid_ThrowsBusinessException() {
        //Arrange
        long orderId = 1L;

        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.getPayment(orderId));

        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo("Pedido não encontrado.");
        verify(mockRepository, times(1)).findById(anyLong());
    }

    private GeneratePaymentDTO generatePaymentDTO() {
        return new GeneratePaymentDTO(1L, BigDecimal.valueOf(10));
    }

    private ProcessPaymentDTO processPaymentDTO() {
        return ProcessPaymentDTO.builder()
                .id(1L)
                .qrCode("NjcyMzgzMDgyMA==")
                .totalValue(BigDecimal.valueOf(10))
                .build();
    }

    private Payment generatePayment() {
        var payment = Payment.generate(1L, BigDecimal.valueOf(10));
        payment.setQrCode("NjcyMzgzMDgyMA==");
        return payment;
    }
}
