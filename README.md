# Tech Challenge MS-PAYMENT

### Esta é uma aplicação com propósito acadêmico.

## Este serviço simula um microsserviço de pagamento. Seus endpoints incluem a geração de informações para pagamento por QR Code e uma rota para simulação de processamento de pagamento.

## Pré-requisitos
- Java SDK 17
- MySQL 

### Health Check

```
http://localhost:8080/actuator/health
```

### Swagger

```
http://localhost:8080/swagger-ui/index.html
```
### Testes

```sh
mvn test
```

## Endpoints

### Gerar QRCode de pagamento
```
[POST]
http://localhost:8080/payment

SCHEMA

{
  "idPedido": int64,
  "valorTotal": number
}
```
><span style="color:red">Observação:</span> Os dados recebidos devem ser persistidos, pois serão validados no momento do processamento do pagamento.
### Processar pagamento
```
[POST]
http://localhost:8080/payment/webhook

SCHEMA

{
  "idPedido": int64,
  "qrCode": "string",
  "valorTotal": number
}
```



><span style="color:red">Observação:</span> O processamento de pagamento requer integração com o serviço de produção para finalizar o fluxo. Essa configuração pode ser realizada nos arquivos application.yml.