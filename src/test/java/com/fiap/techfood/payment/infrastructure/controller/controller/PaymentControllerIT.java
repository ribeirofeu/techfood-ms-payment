package com.fiap.techfood.payment.infrastructure.controller.controller;

import com.fiap.techfood.payment.application.dto.request.ProcessPaymentDTO;
import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Transactional
class PaymentControllerIT {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void givenValidPayment_whenSavePayment_thenShouldReturnProcessPaymentDTO() {
        var generatePaymentDTO = new GeneratePaymentDTO(2L);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .when()
                .post("/payment/generate")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/processPayment.schema.json"));

    }

    @Test
    void givenInvalidOrderId_whenSavePayment_thenShouldReturnBadRequest() {
        var generatePaymentDTO = new GeneratePaymentDTO(-1L);
        var expectedMessage = ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage();
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .when()
                .post("/payment/generate")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo(expectedMessage));
    }

    @Test
    void givenNullOrderId_whenSavePayment_thenShouldReturnBadRequest() {
        var generatePaymentDTO = new GeneratePaymentDTO(null);
        var expectedMessage = ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage();
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .when()
                .post("/payment/generate")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo(expectedMessage));
    }

    @Test
    void givenInvalidProcessPaymentId_whenProcessPayment_thenShouldReturnBadRequest() {
        var processPaymentDto = ProcessPaymentDTO.builder().id(10L).build();
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(processPaymentDto)
                .when()
                .post("/payment/webhook")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Pedido não encontrado."));
    }

    @Test
    void givenPaymentDTO_whenOrderIdIsValid_thenShouldReturnPaymentDTO() {
        long orderId = 2L;
        given()
                .pathParams("orderId",orderId)
                .when()
                .get("/payment/{orderId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/payment.schema.json"));
    }

    @Test
    void givenInvalidPaymentDTO_whenOrderIdIsInvalid_thenShouldReturnBadRequest() {
        long orderId = 10L;
        given()
                .pathParams("orderId",orderId)
                .when()
                .get("/payment/{orderId}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Pedido não encontrado."));
    }
}
