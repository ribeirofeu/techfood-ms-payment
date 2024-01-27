package com.fiap.techfood.payment.application.usecases;

import com.fiap.techfood.payment.application.dto.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.infrastructure.service.Notification;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import com.fiap.techfood.payment.domain.commons.enums.HttpStatusCodes;
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
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
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
        assertThat(processPaymentDto.getId()).isEqualTo(generateQRCode.getOrderId());
        assertThat(processPaymentDto.getTotalValue()).isEqualTo(generateQRCode.getTotalValue());
        assertThat(processPaymentDto.getQrCode()).isNotEmpty();
        verify(mockRepository, times(1)).save(any());
    }

    @Test
    void generatePaymentQRCode_InvalidRequest_WithNegativeId_ThrowsBusinessException() {
        //Arrange
        var generateQRCode = generatePaymentDTO();
        generateQRCode.setOrderId(-1L);

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
        var generateQRCode = generatePaymentDTO();
        generateQRCode.setOrderId(null);

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
        var generateQRCode = generatePaymentDTO();
        generateQRCode.setTotalValue(BigDecimal.valueOf(-1));

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
        var generateQRCode = generatePaymentDTO();
        generateQRCode.setTotalValue(null);

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
        var processPaymentDTO = processPaymentDTO();

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));
        when(mockNotification.send(any()))
                .thenReturn(new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.CREATED));
        doNothing().when(mockRepository).updatePaymentStatus(any());

        //Act
        var paymentDTO = mockPaymentUseCases.processPayment(processPaymentDTO);

        //Assert
        assertThat(paymentDTO).isNotNull();
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(1)).updatePaymentStatus(any());
        verify(mockNotification, times(1)).send(any());
    }

    @Test
    void processPayment_NotFoundProcessPaymentId_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();

        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo("Pedido não encontrado.");
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidProcessPaymentNullTotalValue_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();
        processPaymentDTO.setTotalValue(null);

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidProcessPaymentNegativeTotalValue_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();
        processPaymentDTO.setTotalValue(BigDecimal.valueOf(-1));

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage());
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidProcessPaymentNullQRCode_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();
        processPaymentDTO.setQrCode(null);

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_QRCODE.getMessage());
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidProcessPaymentEmptyQRCode_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();
        processPaymentDTO.setQrCode("");

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.NULL_OR_INVALID_QRCODE.getMessage());
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidProcessPaymentInvalidQRCode_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();
        processPaymentDTO.setQrCode("inválido");

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.BAD_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCodes.INVALID_QRCODE.getMessage());
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(0)).updatePaymentStatus(any());
        verify(mockNotification, times(0)).send(any());
    }

    @Test
    void processPayment_InvalidNotification_ServiceUnavailable_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));
        when(mockNotification.send(any())).thenThrow(new RestClientException("Serviço indisponivel ou URI inválida"));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.SERVICE_UNAVAILABLE.getCode());
        assertThat(exception.getMessage())
                .isEqualTo("Falha ao enviar pedido para produção. Tente novamente mais tarde! :(");
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockNotification, times(1)).send(any());
    }

    @Test
    void processPayment_InvalidNotification_StatusCodeOK_ThrowsBusinessException() {
        //Arrange
        var processPaymentDTO = processPaymentDTO();

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(generatePayment()));
        when(mockNotification.send(any()))
                .thenReturn(new ResponseEntity<>("Mensagem de resposta simulada", HttpStatus.OK));

        //Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> mockPaymentUseCases.processPayment(processPaymentDTO));

        //Assert
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatusCodes.SERVICE_UNAVAILABLE.getCode());
        assertThat(exception.getMessage())
                .isEqualTo("Falha ao enviar pedido para produção. O serviço externo retornou um status inesperado: "
                        + HttpStatus.OK.value());
        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockNotification, times(1)).send(any());
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
