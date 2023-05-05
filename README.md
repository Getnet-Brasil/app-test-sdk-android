# Documentação para utilização do SDK Getnet versão Android

## Instalação

**IMPORTANTE: O SDK foi implementado em Kotlin, portanto os exemplos desta documentação estarão baseados na mesma linguagem.**

Instalação

1.	Adicione o arquivo AAR do SDK Getnet que foi fornecido na subpasta **"libs"** que está no módulo da sua aplicação;
2.	Com seu projeto aberto no Android Studio, clicar em **File -> Project Structure -> Dependencies**;
3.	Selecionar o módulo da sua aplicação em **"Modules"** e clicar em **"+"** em **Declared Dependencies**;
4.	Selecionar a opção **"JAR/AAR Dependency"** e colocar o caminho para o arquivo AAR do SDK Getnet que está na sua pasta libs e clique em **“Apply”**;
5.	Verifique se a seguinte linha foi adicionada no **build.gradle**:
```
implementation files('libs/getnetpd.aar')
```

6.	Navegue até **app/build.gradle** e adicione as seguintes linhas: (Caso já esteja usando algumas destas dependências, verifique se as versões estão iguais as que estão abaixo)

dependencies {

```
/*RETROFIT*/
    	api 'com.squareup.retrofit2:retrofit:2.9.0'
    	api 'com.squareup.retrofit2:converter-gson:2.9.0'
    	api 'com.squareup.okhttp3:logging-interceptor:4.5.0'
    	api 'com.squareup.retrofit2:adapter-rxjava2:2.5.0'
}
```

7.	Adicione a seguinte linha no arquivo **gradle.properties**
```
	android.enableJetifier=true
```

Ao concluir o procedimento, clique em Sync Now. Após sincronizar, seu projeto terá concluído a importação do SDK.

## Ambiente de execução

Antes de usar o SDK é preciso definir em qual ambiente as requisições serão enviadas, podendo ser **HOMOLOGAÇÃO** ou **PRODUÇÃO**.

Exemplo:
```
GPDConfig.setDevEnviroment(GPDEnviroment.HOMOLOG)
```
ou
```
GPDConfig.setDevEnviroment(GPDEnviroment.PROD)
```

## Autenticação na Plataforma Digital Getnet

Para realizar chamadas nos serviços da Getnet, é necessário possuir um token de acesso. A obtenção desse token pode ser realizada seguindo a documentação a seguir: https://developers.getnet.com.br/api#tag/Autenticacao

## Vault
**Descrição:**

Carrega a tela de cofre que cria um recurso cards dentro da plataforma digital Getnet a partir dos dados de cartão do consumidor, para uso em pagamentos futuros.

**Exemplo:**
```
startActivity(
    activity?.let {
        GPDVaultActivity.create(
            context: Context,
            authKey: String,
            customerId: String,
            cardHolderIdentification: String? = null,
            verifyCard: Boolean? = null,
            cardNameVisibility: Boolean = true,
            cardCvvVisibility: Boolean = true
        )
    }
)
```

**Parâmetros:**
```
context                         obritatório             Contexto de execução do Android
authKey                         obritatório             Token de acesso
customerId                      obritatório             Id do cliente
cardHolderIdentification        opcional                Identificador do comprador
verifyCard                      opcional                Realiza uma transação que Verifica se o cartão informado
                                                        não está cancelado, bloqueado ou com restrições
cardNameVisibility              opcional                Indica se o campo cardname deverá ser exibido na tela
cardCvvVisibility               opcional                Indica se o campo cvv deverá ser exibido na tela 
```

## Checkout
**Descrição:**

Carrega a tela de checkout que permite pagamentos com credito, débito, boleto, qrcode, débito caixa e pix.

IMPORTANTE: os métodos de pagamento citados devem estar habilitados no e-commerce para que estejam disponíveis na tela de Checkout


**Exemplo:**
```
startActivity(
    activity?.let {
        GPDCheckoutActivity.create(
            context: Context,
            authKey: String,
            sellerId: String,
            payload: GPDCheckoutModel,
            cardNameVisibility: Boolean = true,
            cardCvvVisibility: Boolean = true,
        )
    }
)
```

**Parâmetros:**
```
context                         obritatório             Contexto de execução do Android
authKey                         obritatório             Token de acesso
sellerId                        obritatório             Código de identificação do e-commerce
payload                         obrigatório             Objeto contendo dados da transação
cardNameVisibility              opcional                Indica se o campo cardname deverá ser exibido na tela
cardCvvVisibility               opcional                Indica se o campo cvv deverá ser exibido na tela 
```


## Gerenciamento de callbacks

As telas de Vault e Checkout usam callbacks para fornecer um retorno do resultado da operação.

Passo-a-passo da implementação:

**1º** A Activity (ou Fragment) que executar uma chamada para Vault ou Checkout deverá implementar a interface ```GPDCallback```

Exemplo:
```
class CheckoutScreenCaller : Fragment(), GPDCallback {
    ...
}
```

**2º** Atibuir a tela chamadora para o atributo ```gpdCallback``` da classe Vault ou Checkout

Exemplo:

```
    GPDCheckoutActivity.gpdCallback = this
```
e
```
    GPDVaultActivity.gpdCallback
```

**3º** Fornecer uma implementação para os métodos da inferface

```
    fun onUnauthorizedResponse(activity: AppCompatActivity)

    fun closedWithSuccess(details: JsonObject?)

    fun closedWithError(details: JsonObject?)

    fun closedByUser()
```

Descrição dos métodos:

**fun onUnauthorizedResponse(activity: AppCompatActivity)**<br>
Método chamado quando o serviço retorna 401

**fun closedWithSuccess(details: JsonObject?)**<br>
Método chamado em caso de sucesso. Pode receber um JSON com o payload de retorno.

**fun closedWithError(details: JsonObject?)**<br>
Método chamado quando acontece algum erro em chamada de serviço. Pode receber um JSON com o payload de erro.

**fun closedByUser()**<br>
Método chamado quando o usuário fecha a tela do SDK voluntariamente.


## Tokenização de cartão

**Descrição:**

Para efetuar transações de forma segura os dados do cartão, exceto CVV, devem ser tokenizados, esse processo é feito pelo serviço descrito abaixo mediante autenticação.

**Definição**:

```
GPDCard.token(
    authKey: String,
    cardNumber: String,
    customerId: String? = null,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
cardNumber          obrigatório             Número do cartão a ser tokenizado
customerId          opcional                Id do cliente
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Exemplo:**

```
GPDCard.token(
    authKey = authKey,
    cardNumber = "5200000000001096",
    customerId = "customer"
) { response ->
    // Handle json response here
}
```

## Cofre

### Armazer novo cartão

**Descrição:**

Armazena os dados de cartão do consumidor no cofre


**Definição**:

```
GPDCard.add(
    authKey: String, 
    card: GPDCardModel, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
card                obrigatório             Objeto contendo os dados do cartão
callback:           obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado nesse ponto da documentação da Getnet: https://developers.getnet.com.br/api#tag/Cofre%2Fpaths%2F~1v1~1cards%2Fpost

**Exemplo:**

```

val card = GPDCardModel(
    numberToken = edtCardNumberToken.text.toString(),
    expirationMonth = edtCardExpirationMonth.text.toString(),
    expirationYear = edtCardExpirationYear.text.toString(),
    customerId = edtCardCustomerId.text.toString(),
    cardholderName = edtCardCardholderName.text.toString(),
    brand = edtCardBrand.text.toString(),
    cardholderIdentification = edtCardholderIdentification2.text.toString(),
    verifyCard = rbVerifyCardTrue.isChecked,
    securityCode = edtCardSecurityCode.text.toString()
)

GPDCard.add(
    authKey = authKey,
    card = card
) { response ->
    // Handle json response here
}

```

### Lista cartões armazenados

**Descrição:**

Lista todos os cartões que estão armazenados no cofre.


**Definição**:

```
GPDCard.get(
    authKey: String,
    customerId: String,
    sellerId: String? = null,
    status: GPDCardStatus? = null,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
customerId          obrigatório             Id do cliente
sellerId            opcional                Código de identificação do e-commerce
status              opcional                Status do cartão no cofre
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado nesse ponto da documentação da Getnet: https://developers.getnet.com.br/api#tag/Cofre%2Fpaths%2F~1v1~1cards%2Fget

**Exemplo:**

```
GPDCard.get(
    authKey = authKey,
    sellerId = sellerId,
    customerId = edtCardCustomerIdList.text.toString(),
    status = GPDCardStatus.valueOf(spCardStatus.selectedItem.toString())
) { response ->
    // Handle json response here
}
```

### Recupera um cartão

**Descrição:**

Recupera os dados de um cartão específico

**Definição**:

```
GPDCard.get(
    authKey: String,
    cardId: String,
    sellerId: String? = null,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
cardId              obrigatório             Id do cartão que será resgatado
sellerId            opcional                Código de identificação do e-commerce
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado nesse ponto da documentação da Getnet: https://developers.getnet.com.br/api#tag/Cofre%2Fpaths%2F~1v1~1cards~1%7Bcard_id%7D%2Fget

**Exemplo:**

```
GPDCard.get(
    authKey = authKey,
    sellerId = sellerId,
    cardId = edtCardId.text.toString()
) { response ->
    // Handle json response here
}
```

### Remove um cartão

**Descrição:**

Remove um cartão que está armazenado no cofre.

**Definição**:

```
GPDCard.delete(
    authKey: String,
    cardId: String,
    sellerId: String? = null,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
cardId              obrigatório             Id do cartão que será resgatado
sellerId            opcional                Código de identificação do e-commerce
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado nesse ponto da documentação da Getnet: https://developers.getnet.com.br/api#tag/Cofre%2Fpaths%2F~1v1~1cards~1%7Bcard_id%7D%2Fdelete

**Exemplo:**

```
GPDCard.delete(
    authKey = authKey,
    sellerId = sellerId,
    cardId = edtCardId.text.toString()
) { response ->
    // Handle json response here
}
```

## Pagamento Crédito

### Verirficar cartão

**Descrição:**

O objetivo da transação de verificação de cartão de crédito é verificar se o cartão de crédito informado pelo portador é um cartão válido. Entende-se como um cartão crédito válido um cartão que não está cancelado, bloqueado ou com restrições.

**Definição**:

```
GPDCard.verify(
    authKey: String,
    card: GPDCardModel,
    sellerId: String? = null,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
card                obrigatório             Objeto contendo os dados do cartão
sellerId            opcional                Código de identificação do e-commerce
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado nesse ponto da documentação da Getnet: https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1cards~1verification%2Fpost

**Exemplo:**

```
val card = GPDCardModel(
    numberToken = edtCardNumberToken.text.toString(),
    expirationMonth = edtCardExpirationMonth.text.toString(),
    expirationYear = edtCardExpirationYear.text.toString(),
    cardholderName = edtCardCardholderName.text.toString(),
    securityCode = edtSecurityCodeCredit.text.toString(),
    brand = edtCardBrand.text.toString()
)

GPDCard.verify(
    authKey = authKey,
    sellerId = sellerId,
    card = card
) { response ->
    // Handle json response here
}
```

### Pagamento
**Descrição:**

Nesse serviço será enviado dados para pagamento com cartão de crédito.

Para o os campos delayed e pre_authorization, somente um deles podem estar com valor true, indicando assim somente um serviço de crédito a ser processado.

**Definição**:

```
GPDPaymentCredit.create(
    authKey: String, 
    payment: GPDPaymentCreditModel, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
payment             obrigatório             Objeto com dados sobre o pagamento com cartão de crédito
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado nesse ponto da documentação da Getnet: https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1credit%2Fpost

**Exemplo:**

```
val order = GPDOrderModel(
    orderId = edtOrderIdPaymentCredit.text.toString(),
    salesTax = edtSalesTaxPaymentCredit.text.toString().toInt(),
    productType = spProductTypePaymentCredit.selectedItem.toString()
)

val billingAddress = GPDAddressModel(
    street = edtStreetBillingAddressPaymentCredit.text.toString(),
    number = edtNumberBillingAddressPaymentCredit.text.toString(),
    complement = edtComplementBillingAddressPaymentCredit.text.toString(),
    district = edtDistrictBillingAddressPaymentCredit.text.toString(),
    city = edtCityBillingAddressPaymentCredit.text.toString(),
    state = edtStateBillingAddressPaymentCredit.text.toString(),
    country = edtCountryBillingAddressPaymentCredit.text.toString(),
    postalCode = edtPostalCodeBillingAddressPaymentCredit.text.toString()
)

val customer = GPDCustomerModel(
    customerId = edtCustomerIdPaymentCredit.text.toString(),
    firstName = edtFirstNamePaymentCredit.text.toString(),
    lastName = edtLastNamePaymentCredit.text.toString(),
    name = edtNamePaymentCredit.text.toString(),
    email = edtEmailPaymentCredit.text.toString(),
    documentType = GPDDocumentType.valueOf(spDocumentTypePaymentCredit.selectedItem.toString()),
    documentNumber = edtDocumentNumberPaymentCredit.text.toString(),
    phoneNumber = edtPhoneNumberPaymentCredit.text.toString(),
    billingAddress = billingAddress
)

val card = GPDCardModel(
    numberToken = edtCardNumberToken.text.toString(),
    cardholderName = edtCardCardholderName.text.toString(),
    securityCode = edtSecurityCodeCredit.text.toString(),
    brand = edtCardBrand.text.toString(),
    expirationYear = edtCardExpirationYear.text.toString(),
    expirationMonth = edtCardExpirationMonth.text.toString()
)

val credit = GPDCreditModel(
    delayed = edtDelayedPaymentCredit.text.toString().toBoolean(),
    preAuthorization = edtPreAuthPaymentCredit.text.toString().toBoolean(),
    saveCardData = edtSaveCardDataPaymentCredit.text.toString().toBoolean(),
    transactionType = GPDTransactionType.valueOf(edtTransactionTypePaymentCredit.text.toString()),
    numberInstallments = edtNumberInstallmentsPaymentCredit.text.toString().toInt(),
    softDescriptor = edtSoftDescriptorPaymentCredit.text.toString(),
    dynamicMcc = edtDynamicMccPaymentCredit.text.toString().toInt(),
    card = card
)

val payment = GPDPaymentCreditModel(
    sellerId = sellerId,
    amount = edtAmountPaymentCredit.text.toString().toInt(),
    currency = edtCurrencyPaymentCredit.text.toString(),
    order = order,
    customer = customer,
    credit = credit
)

GPDPaymentCredit.create(
    authKey = authKey,
    payment = payment
) { response ->
    // Handle json response here
}
```

### Confirmação tardia

**Descrição:**

Quando uma transação de crédito é feita com a opção delayed = true ou pre_authorization = true, confirmação tardia ou pré autorização, respectivamente, ela pode ser posteriormente confirmada. Esta chamada é responsável pela confirmação.


**Definição**:

```
GPDPaymentCredit.confirm(
    authKey: String, 
    paymentId: String, 
    amount: Int, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
paymentId           obrigatório             Identificador do pagamento previamente recebido e aceito.
amount              obrigatório             Valor de confirmação. Pode ser igual ou menor que o valor original 
                                            da transação. Caso não seja informado, será acatado o valor 
                                            original da transação.
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1credit~1%7Bpayment_id%7D~1confirm%2Fpost) ponto da documentação da Getnet.

**Exemplo:**
```
GPDPaymentCredit.confirm(
    authKey = authKey,
    paymentId = edtPaymentIdCredit.text.toString(),
    amount = edtAmountCreditManage.text.toString().toInt()
) { response ->
    // Handle json response here
}
```

### Ajustar valor

**Descrição:**

Executa um ajuste (Incremento/Decremento) no valor previamente reservado no saldo do Portador por uma Transação de Pré-Autorização. O valor da Transação de Ajuste de Pré-Autorização pode ser maior ou menor que o valor original. Na chamada do processo de ajuste sempre deve ser enviado o valor final desejado no campo de amount.

**Definição**:

```
GPDPaymentCredit.adjust(
    authKey: String, 
    paymentId: String, 
    adjust: GPDAdjustCreditModel, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
paymentId           obrigatório             Identificador do pagamento previamente recebido e aceito.
adjust              obrigatório             Objeto contendo dados sobre o ajuste.
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1credit~1%7Bpayment_id%7D~1adjustment%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val adjustment = GPDAdjustCreditModel(
    amount = edtAmountCreditManage.text.toString().toInt(),
    softDescriptor = edtSoftDescriptorPaymentCreditManage.text.toString(),
    dynamicMcc = edtDynamicMccPaymentCreditManage.text.toString().toInt(),
    currency = edtCurrencyPaymentCreditManage.text.toString()
)

GPDPaymentCredit.adjust(
    authKey = authKey,
    paymentId = edtPaymentIdCredit.text.toString(),
    adjust = adjustment
) { response ->
    // Handle json response here
}
```

### Cancelamento

**Descrição:**
Cancelar transação de crédito completa ou cancelar/desfazer transação de crédito tardio ou transação de pré autorização. Caso a transação já esteja confirmada, o cancelamento é permitido apenas no mesmo dia (D0) em que esta transação foi realizada. No entanto se a transação encontra-se autorizada (transação de crédito tardio ou pré autorização) o desfazimento pode ser realizado em até 7 dias.

Estorna ou desfaz transações feitas no mesmo dia (D0).

**Definição**:

```
GPDPaymentCredit.cancel(
    authKey: String, 
    paymentId: String, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
paymentId           obrigatório             Identificador do pagamento previamente recebido e aceito.
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1credit~1%7Bpayment_id%7D~1cancel%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
GPDPaymentCredit.cancel(
    authKey = authKey,
    paymentId = edtPaymentIdCredit.text.toString()
) { response ->
    // Handle json response here
}
```

## Pagamento Débito

### Pagamento

**Descrição:**

Nesse serviço será enviado dados para pagamento com cartão de débito.

**Definição**:

```
GPDPaymentDebit.create(
    authKey: String, 
    payment: GPDPaymentDebitModel, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
payment             obrigatório             Objeto com dados sobre o pagamento com cartão de débito
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1debit%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val order = GPDOrderModel(
    orderId = edtOrderIdPaymentCredit.text.toString(),
    salesTax = edtSalesTaxPaymentCredit.text.toString().toInt(),
    productType = spProductTypePaymentCredit.selectedItem.toString()
)

val billingAddress = GPDAddressModel(
    street = edtStreetBillingAddressPaymentCredit.text.toString(),
    number = edtNumberBillingAddressPaymentCredit.text.toString(),
    complement = edtComplementBillingAddressPaymentCredit.text.toString(),
    district = edtDistrictBillingAddressPaymentCredit.text.toString(),
    city = edtCityBillingAddressPaymentCredit.text.toString(),
    state = edtStateBillingAddressPaymentCredit.text.toString(),
    country = edtCountryBillingAddressPaymentCredit.text.toString(),
    postalCode = edtPostalCodeBillingAddressPaymentCredit.text.toString()
)

val customer = GPDCustomerModel(
    customerId = edtCustomerIdPaymentCredit.text.toString(),
    firstName = edtFirstNamePaymentCredit.text.toString(),
    lastName = edtLastNamePaymentCredit.text.toString(),
    name = edtNamePaymentCredit.text.toString(),
    email = edtEmailPaymentCredit.text.toString(),
    documentType = GPDDocumentType.valueOf(spDocumentTypePaymentCredit.selectedItem.toString()),
    documentNumber = edtDocumentNumberPaymentCredit.text.toString(),
    phoneNumber = edtPhoneNumberPaymentCredit.text.toString(),
    billingAddress = billingAddress
)

val card = GPDCardModel(
    numberToken = edtCardNumberToken.text.toString(),
    cardholderName = edtCardCardholderName.text.toString(),
    securityCode = edtSecurityCodeCredit.text.toString(),
    brand = edtCardBrand.text.toString(),
    expirationYear = edtCardExpirationYear.text.toString(),
    expirationMonth = edtCardExpirationMonth.text.toString()
)

val debit = GPDDebitModel(
    softDescriptor = edtSoftDescriptorPaymentCredit.text.toString(),
    dynamicMcc = edtDynamicMccPaymentCredit.text.toString().toInt(),
    card = card,
    cardholderMobile = edtCardCardholderName.text.toString()
)

val payment = GPDPaymentDebitModel(
    sellerId = sellerId,
    amount = edtAmountPaymentCredit.text.toString().toInt(),
    currency = edtCurrencyPaymentCredit.text.toString(),
    order = order,
    customer = customer,
    debit = debit
)

GPDPaymentDebit.create(
    authKey = authKey,
    payment = payment
) { response ->
    // Handle json response here
}
```

### Cancelamento

**Descrição:**

Cancelar uma transação de debito completo.
Estorna ou desfaz transações feitas no mesmo dia (D0).

**Definição**:

```
GPDPaymentDebit.cancel(
    authKey: String, 
    paymentId: String, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
paymentId           obrigatório             Identificador do pagamento previamente recebido e aceito.
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1debit~1%7Bpayment_id%7D~1cancel%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
GPDPaymentDebit.cancel(
    authKey = authKey,
    paymentId = edtPaymentIdDebit.text.toString()
) { response ->
    // Handle json response here
}
```

## Boleto

### Geração

**Descrição:**

Nessa chamada serão enviados os dados do novo boleto, que será registrado no Banco Santander.


Impressão da segunda via de um boleto
No registro de um pagamento com boleto é retornado o campo payment_id na resposta da requisição de registro. O payment_id será utilizado para a requisição de download da segunda via do boleto nas requisições Download pdf do boleto ou Download html do boleto

**Definição**:

```
GPDPaymentBoleto.createV1(
    authKey: String, 
    sellerId: String,
    payment: GPDPaymentBoletoModel, 
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Token de acesso
sellerId            obrigatório             Código de identificação do e-commerce
payment             obrigatório             Objeto com dados sobre o pagamento com boleto
callback            obrigatório             Callback que receberá o retorno da chamada em JSON
```

**Retorno:**

O JSON de retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1boleto%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val boletoPayload = GPDPaymentBoletoModel(
    amount = 1000,
    order = GPDOrderModel(
        orderId = edtOrderIdBoleto.text.toString(),
        salesTax = 0,
        productType = spProductTypeBoleto.selectedItem.toString()
    ),
    boleto = GPDBoletoModel(
        ourNumber = edtOurNumberBoleto.text.toString(),
        documentNumber = edtDocumentNumberBoleto.text.toString(),
        expirationDate = edtExpirationDateBoleto.text.toString(),
        instructions = edtInstructionsBoleto.text.toString(),
        provider = edtProviderBoleto.text.toString()
    ),
    customer =  GPDCustomerModel(
        name = edtFirstNameBoleto.text.toString(),
        email = edtEmailBoleto.text.toString(),
        documentType = GPDDocumentType.CPF,
        documentNumber = edtDocumentNumberCustomerBoleto.text.toString(),
        billingAddress = GPDAddressModel(
            street = edtStreetBoleto.text.toString(),
            number = edtNumberBoleto.text.toString(),
            district = edtDistrictBoleto.text.toString(),
            city = edtCityBoleto.text.toString(),
            state = edtStateBoleto.text.toString(),
            country = edtCountryBoleto.text.toString(),
            postalCode = edtPostalCodeBoleto.text.toString(),
            complement = edtComplementBoleto.text.toString()
        ),
        firstName = edtFirstNameBoleto.text.toString()
    ),
    currency = "BRL"
)

GPDPaymentBoleto.createV1(
    sellerId = sellerId,
    authKey = authKey,
    payment = boletoPayload
) { response ->
    // Handle json response here
}
```

### Download PDF

**Descrição:**

Nessa chamada o usuário consegue realizar o download PDF do boleto para o ID informado.

**Definição**:

```
GPDPaymentBoleto.getPdfV1(
    paymentId: String, 
    callback: (Response<ResponseBody>?) -> Unit
)
```

**Parâmetros:**

```
paymentId           obrigatório             Identificador do pagamento previamente recebido e aceito.
callback            obrigatório             Callback que receberá o retorno da chamada.       
```

**Retorno:**

O retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1boleto~1%7Bpayment_id%7D~1pdf%2Fget) ponto da documentação da Getnet.

**Exemplo:**

```
GPDPaymentBoleto.getPdfV1(
    paymentId = edtPaymentIdBoleto.text.toString(),
) { response ->
    // Handle json response here
}
```

### Download HTML

**Descrição:**

Nessa chamada o usuário consegue realizar o download HTML do boleto para o ID informado.

**Definição**:

```
GPDPaymentBoleto.getHtmlV1(
    paymentId: String, 
    callback: (Response<ResponseBody>?) -> Unit
)
```

**Parâmetros:**

```
paymentId           obrigatório             Identificador do pagamento previamente recebido e aceito.
callback            obrigatório             Callback que receberá o retorno da chamada.       
```

**Retorno:**

O retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento%2Fpaths%2F~1v1~1payments~1boleto~1%7Bpayment_id%7D~1html%2Fget) ponto da documentação da Getnet.

**Exemplo:**

```
GPDPaymentBoleto.getHtmlV1(
    paymentId = edtPaymentIdBoleto.text.toString(),
) { response ->
    callback(response)
}
```
## Autenticação 3DS 2.1

### Geração de Token 3DS

**Descrição:**

Chamada responsável pela geração do token de acesso para abertura de uma sessão de autenticação 3DS junto à GetNet.

**Definição**:

```
GPDAuthentication3ds.generateToken(
    authKey: String,
    sellerId: String,
    order: GPDOrderModel3ds,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Identificador do pagamento previamente recebido e aceito.
sellerId            opcional                Código de identificação do e-commerce.
order               obrigatório             Objeto contento dados que identificam a sessão e a compra 
                                            que está sendo efetuada.
callback            obrigatório             Callback que receberá o retorno da chamada.       
```

**Retorno:**

O retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Autenticacao-3DS%2Fpaths%2F~1v1~13ds~1tokens%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val generateTokenBody = GPDOrderModel3ds(
    clientCode = "string",
    currency = "BRL",
    jsVersion = "1.0.0",
    orderNumber = "1234-2019",
    overridePaymentMethod = "02",
    totalAmount = 100,
    items = listOf(GPDOrderItem3ds(
        name = "name",
        description = "description",
        quantity = 1,
        unitPrice = 1,
        sku = "sku",
        totalAmount = 1000
    ))
)
        
GPDAuthentication3ds.generateToken(
    authKey = authKey,
    sellerId = sellerId,
    order = generateTokenBody
) { response ->
    callback(response)
}
```

### Solicita autenticação 3DS

**Descrição:**

Chamada responsável pela solicitação de autenticação 3DS junto à GetNet.

**Definição**:

```
GPDAuthentication3ds.getAuthentications(
    authKey: String,
    sellerId: String? = null,
    authentications: GPDAuthentications3ds,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey             obrigatório             Identificador do pagamento previamente recebido e aceito.
sellerId            opcional                Código de identificação do e-commerce.
authentications     obrigatório             Objeto contento dados que identificam a compra que está sendo 
                                            efetuada (Order), detalhes sobre autenticação, histórico do cliente, 
                                            dados do cartão.
callback            obrigatório             Callback que receberá o retorno da chamada.       
```

**Retorno:**

O retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Autenticacao-3DS%2Fpaths%2F~1v1~13ds~1authentications%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val authentication = GPDAuthentication(
    token = token,
    npaCode = "01",
    challengeCode = "01",
    installmentTotalCount = 3,
    messageCategory = "01",
    transactionMode = "M",
    deviceChannelSnakeCase = "SDK",
    acsWindowSize = "02"
)

val account = GPDAccount(
    modificationHistory = "ACCOUNTUPDATEDNOW",
    accountPurchases = 4,
    shippingAddressUsageDate = "23/11/2022",
    firstUseOfShippingAddress = false,
    lastChangeDate = "23/11/2022",
    createdDate = "23/11/2022",
    creationHistory = "NEW_ACCOUNT",
    passwordChangedDate = "2019-11-25",
    passwordHistory = "PAYMENTACCOUNTEXISTS"
)

val customerRiskInfos = GPDCustomerRiskInfos(
    transactionCountYear = 12,
    transactionCountDay = 1,
    addCardAttempts = 1,
    customerId = "246ac61a-60da-40d5-9bad-2935f210385e",
    customerTypeId = "CPF",
    paymentAccountHistory = "PAYMENTACCOUNTEXISTS",
    paymentAccountDate = "20191125",
    priorSuspiciousActivity = "false",
    account = account
)

val recurring = GPDRecurring(
    endDate = "2019-11-25",
    frequency = 4,
    originalPurchaseDate = "2019-11-25"
)

val card = GPDAuthenticationCard(
    numberToken = tokenized_card,
    expirationMonth = cardExpiration.split("/")[0].trim(),
    expirationYear = cardExpiration.split("/")[1].trim(),
    typeCard = "002",
    defaultCard = false
)

val billTo = GPDBillTo()
billTo.firstName = "João"
billTo.lastName = "da Silva"
billTo.locality = "Porto Alegre"
billTo.administrativeArea = "RS"
billTo.country = "BR"
billTo.address1 = "Av. Brasil, 1000, Sala 1"
billTo.address2 = "São Geraldo"
billTo.postalCode = "90230060"
billTo.mobilePhone = "5581979001904"

val shipTo = GPDShipTo()
shipTo.firstName = "Lucas"
shipTo.lastName = "Lucas Grando"
shipTo.locality = "Porto Alegre"
shipTo.administrativeArea = "RS"
shipTo.country = "BR"
shipTo.address1 = "Av. Brasil, 1000"
shipTo.address2 = "São Geraldo"
shipTo.postalCode = "90230060"
shipTo.method = "lowcost"
shipTo.destinationCode = "02"

val order = GPDAuthenticationOrder(
    productCode = "01",
    currency = "BRL",
    totalAmount = 1000,
    billTo = billTo,
    shipTo = shipTo,
    items = parseItems()
)

val authentications = GPDAuthentications3ds(
    customerCardAlias = "João da Silva",
    overridePaymentMethod = "02",
    alternateAuthenticationData = "string",
    alternateAuthenticationMethod = "02",
    authentication = authentication,
    device = null,
    customerRiskInformation = customerRiskInfos,
    recurring = recurring,
    card = card,
    order = order
)


GPDAuthentication3ds.getAuthentications(
    authKey = authKey,
    authentications = authentications
) { response ->
    callback(response)
}
```

### Solicita resultado da autenticação 3DS

**Descrição:**

Chamada responsável pela solicitação do resultado da autenticação 3DS junto à GetNet..

**Definição**:

```
GPDAuthentication3ds.getAuthenticationsResult(
    authKey: String,
    authenticationsResult: GPDAuthenticationResult,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey                     obrigatório             Identificador do pagamento previamente recebido e aceito.
authenticationsResult       obrigatório             Objeto contento dados que identificam a sessão 
                                                    e a compra que está sendo efetuada.
callback                    obrigatório             Callback que receberá o retorno da chamada.       
```

**Retorno:**

O retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Autenticacao-3DS%2Fpaths%2F~1v1~13ds~1results%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val card = GPDAuthenticationCard(
    numberToken = numberToken,
    expirationMonth = cardExpiration.split("/")[0].trim(),
    expirationYear = cardExpiration.split("/")[1].trim(),
    typeCard = "002"
)

val authenticationsResult = GPDAuthenticationResult(
    currency = "BRL",
    overridePaymentMethod = "02",
    token = token,
    tokenChallenge = serverJWT,
    totalAmount = 1000,
    card = card,
    additionalData = JsonObject(),
    additionalObject = JsonObject()
)

GPDAuthentication3ds.getAuthenticationsResult(
    authKey = authKey,
    authenticationsResult = authenticationsResult
) { response ->
    callback(response)
}
```

## Pagamento autenticado 3DS 2.1

### Pagamento autenticado 3DS

**Descrição:**

Nessa chamada serão recebidos dados para pagamento autenticado 3DS com cartão de crédito ou débito.

**Definição**:

```
GPDPayment3ds.requestPaymentAuthenticated(
    authKey: String,
    sellerId: String,
    payment: GPDPaymentAuthenticated,
    callback: (JsonObject) -> Unit
)
```

**Parâmetros:**

```
authKey         obrigatório             Identificador do pagamento previamente recebido e aceito.
sellerId        opcional                Código de identificação do e-commerce.
payment         obrigatório             Objeto contento dados do pagamento que será efetuado.
callback        obrigatório             Callback que receberá o retorno da chamada.       
```

**Retorno:**

O retorno estará no formato encontrado [nesse](https://developers.getnet.com.br/api#tag/Pagamento-Autenticado-3DS%2Fpaths%2F~1v1~1payments~1authenticated%2Fpost) ponto da documentação da Getnet.

**Exemplo:**

```
val card = GPDPaymentAuthenticatedCard(
    numberToken = numberToken,
    brand = "Mastercard",
    securityCode = "123",
    cardholderName = "teste",
    expirationYear = "50",
    expirationMonth = "12"
)

val payment = GPDPaymentAuthenticated(
    orderId = "6d2e4380-d8a3-4ccb-9138-c289182818a3",
    amount = 19990,
    currency = "BRL",
    transactionType = "FULL",
    numberInstallments = 1,
    xid = "string",
    ucaf = ucaf,
    eci = eci,
    tdsdsxid = tdsdsxid,
    tdsver = "2.1.0",
    paymentMethod = paymentMethod,
    dynamicMcc = "1799",
    softDescriptor = "Descrição para fatura",
    customerId = "string",
    credentialsOnFileType = "ONE_CLICK",
    card = card
)

GPDPayment3ds.requestPaymentAuthenticated(
    authKey = authKey,
    sellerId = sellerId,
    payment = payment
) { response ->
    callback(response)
}
```

## Chamadas necessárias para fluxo 3DS 2.1

As seguintes chamadas fazem parte do fluxo 3DS apesar de não se ligarem diretamente com a API da Getnet.

### Criação de instância 3DS

**Descrição:**

Cria a instância para comunicação 3DS.

**Definição**:

```
fun tdsConfigure(context: Context, env: String): GPDInstanceTDS
```

**Parâmetros:**

```
context         obrigatório             Contexto de execução do Android.
env             obrigatório             Ambiente de execução. "PROD" para produção e "HOMOLOG" para homologação
```

**Retorno:**

Retorna um objeto do tipo GPDInstanceTDS.

**Exemplo:**

```
val tdsInstance = GPDAuthentication3ds.tdsConfigure(requireContext(), "PROD")
```

### Inicialização de sessão 3DS

**Descrição:**

Inicia a comunicação 3DS.

**Definição**:

```
GPDAuthentication3ds.tdsInit(
    generatedTokenResponse: String,
    tdsInstance: GPDInstanceTDS,
    onSuccess: (consumerSessionId: String) -> Unit,
    onError: (p0: ValidateResponse?, p1: String?) -> Unit
)
```

**Parâmetros:**

```
generatedTokenResponse      obrigatório     Token 3ds gerado anteriormente
tdsInstance                 obrigatório     Instância do 3ds criada anteriormente
onSuccess                   obrigatório     Callback para sucesso da execução
onError                     obrigatório     Callback para erro na execução
```

**Retorno:**

Sem retorno

**Exemplo:**

```
GPDAuthentication3ds.tdsInit(
    generatedTokenResponse = token!!,
    tdsInstance = tdsInstance,
    onSuccess = { consumerSessionId -> 
        //success handle code
    },
    onError = {p0, p1 ->
        //error handle code 
    })
```

### Exibição de tela de desafio 3DS

**Descrição:**

Renderiza a tela com desafio 3DS.

**Definição**:

```
GPDAuthentication3dstdsShowChallengeScreen(
    activity: Activity,
    tdsInstance: GPDInstanceTDS,
    transactionId: String,
    challenge: String,
    onSuccess: (context: Context, validateResponse: ValidateResponse, s: String?) -> Unit,
    onError: (context: Context, validateResponse: ValidateResponse, s: String?) -> Unit,
    onTimeout: ((context: Context, validateResponse: ValidateResponse, s: String?) -> Unit)?,
    onFailure: ((context: Context, validateResponse: ValidateResponse, s: String?) -> Unit)?,
    onCancel: ((context: Context, validateResponse: ValidateResponse, s: String?) -> Unit)?
)
```

**Parâmetros:**

```
activity            obrigatório     Activity na qual as chamadas estão sendo executadas
tdsInstance         obrigatório     Instância do 3ds criado anteriormente
transactionId:      obrigatório     Código da transação. Pode ser obtido no retorno da autenticação 3ds
challenge           obrigatório     Código que gera o desafio. Pode ser obtido no retorno da autenticação 3ds
onSuccess           obrigatório     Callback para sucesso da execução
onError             obrigatório     Callback para erro da execução
onTimeout           opcional        Callback para timeout da execução
onFailure           opcional        Callback para falha na comunicação 3ds
onCancel            opcional        Callback para cancelamento da execução
```

**Retorno:**

Sem retorno

**Exemplo:**

```
GPDAuthentication3ds.tdsShowChallengeScreen(
    activity = requireActivity()!!,
    tdsInstance = tdsInstance,
    transactionId = transactionId!!,
    challenge = challenge!!,
    onSuccess = { _, validateResponse, jwtToken ->
        //success handle code
    },
    onError = { _, validateResponse, jwtToken ->
        //error handle code
    },
    onTimeout = { _, validateResponse, jwtToken ->
        //timeout handle code
    },
    onCancel = { _, validateResponse, jwtToken ->
        //cancel handle code
    },
    onFailure = { _, validateResponse, jwtToken ->
        //failure handle code
    }
)
```

### Fluxo 3DS sugerido

Para obter sucesso na execução de um pagamento com autenticação 3DS, sugerimos seguir o seguinte fluxo:

```
1º      Criar instância 3DS
2º      Gerar token de acesso 3DS
3º      Inicializar comunicação 3DS
4º      Tokenizar cartão utilizado
5º      Chamar o serviço de autenticação 3DS
6º      Tokenizar cartão novamente
7º      Exibir tela de desafio
8º      Chamar o serviço de resultado da autenticação 3DS
9º      Tokenizar cartão novamente
10º     Chamar o serviço de pagamento autenticado 3DS
```
