# language: pt

  Funcionalidade: Payment

    Cenario: Gerar QRCode de pagamento
      Dado que inicio o processo de pagamento de um pedido
      Então é gerado o QRCode de pagamento e as infomação são salvas na base de dados
      E recebo os dados para realizar o pagamento como um QRCode

    Cenario: Número do pedido inválido
      Dado que informo um número de pedido negativo
      Então é retornado uma mensagem infrmando o erro

    Cenário: Número do pedido vazio
      Dado que não informo um número de pedido
      Então deverá ser retornado uma mensagem informativa

    Cenário: Valor total do pedido negativo
      Dado que informe o valor total do pedido negativo
      Então deverá ser retornado uma mensagem de erro

    Cenário: Sem valor total do pedido
      Dado que não informe o valor total do pedido
      Então deverá ser retornado uma mensagem de erro informativo