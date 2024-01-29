package com.fiap.techfood.payment.bdd;

import com.fiap.techfood.payment.application.dto.request.GeneratePaymentDTO;
import com.fiap.techfood.payment.domain.commons.enums.ErrorCodes;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.core.IsEqual.equalTo;

public class StepDefinition {

    private Response response;

    private final String ENDPOINT_API = "http://localhost:8080/payment/generate";

    @Dado("que inicio o processo de pagamento de um pedido")
    public void que_inicio_o_processo_de_pagamento_de_um_pedido() {
        var generatePaymentDTO = new GeneratePaymentDTO(1L, BigDecimal.valueOf(10.5));
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .post(ENDPOINT_API);
    }

    @Então("é gerado o QRCode de pagamento e as infomação são salvas na base de dados")
    public void é_gerado_o_qr_code_de_pagamento_e_as_infomação_são_salvas_na_base_de_dados() {
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @Então("recebo os dados para realizar o pagamento como um QRCode")
    public void recebo_os_dados_para_realizar_o_pagamento_como_um_qr_code() {
        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/processPayment.schema.json"));
    }

    @Dado("que informo um número de pedido negativo")
    public void que_informo_um_número_de_pedido_negativo() {
        var generatePaymentDTO = new GeneratePaymentDTO(-1L, BigDecimal.valueOf(10.5));
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .post(ENDPOINT_API);
    }

    @Então("é retornado uma mensagem infrmando o erro")
    public void é_retornado_uma_mensagem_infrmando_o_erro() {
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage()));
    }

    @Dado("que não informo um número de pedido")
    public void que_não_informo_um_número_de_pedido() {
        var generatePaymentDTO = new GeneratePaymentDTO(null, BigDecimal.valueOf(10.5));
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .post(ENDPOINT_API);
    }

    @Então("deverá ser retornado uma mensagem informativa")
    public void deverá_ser_retornado_uma_mensagem_informativa() {
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo(ErrorCodes.NULL_OR_INVALID_ORDER_NUMBER.getMessage()));
    }

    @Dado("que informe o valor total do pedido negativo")
    public void que_informe_o_valor_total_do_pedido_negativo() {
        var generatePaymentDTO = new GeneratePaymentDTO(1L, BigDecimal.valueOf(-1));
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .post(ENDPOINT_API);
    }

    @Então("deverá ser retornado uma mensagem de erro")
    public void deverá_ser_retornado_uma_mensagem_de_erro() {
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage()));
    }

    @Dado("que não informe o valor total do pedido")
    public void que_não_informe_o_valor_total_do_pedido() {
        var generatePaymentDTO = new GeneratePaymentDTO(1L, null);
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(generatePaymentDTO)
                .post(ENDPOINT_API);
    }

    @Então("deverá ser retornado uma mensagem de erro informativo")
    public void deverá_ser_retornado_uma_mensagem_de_erro_informativo() {
        response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo(ErrorCodes.NULL_OR_INVALID_TOTAL_VALUE.getMessage()));
    }
}
