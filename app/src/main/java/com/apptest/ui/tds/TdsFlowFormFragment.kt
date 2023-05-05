package com.apptest.ui.tds

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.apptest.R
import com.apptest.services.ServiceRepository
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.managers.GPDAuthentication3ds
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDPayment3ds
import com.getnetpd.models.TokenPayload
import com.getnetpd.models.client.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_tds_flow.*
import java.util.*
import kotlin.collections.ArrayList

class TdsFlowFormFragment : Fragment() {

    companion object {
        private const val TAG = "TdsFlowFragment"
    }
    private var authKey = ""
    private var env: String? = "HOMOLOG"
    private var loadingDialog: LoadingDialog? = null

    private var oauth: String = ""
    private var sellerId: String = ""
    private var clientId: String = ""
    private var clientSecret: String = ""

    private var cardNumber: String = ""
    private var cardExpiration: String = ""
    private var paymentMethod: String = ""
    private var cardCvv: String = ""
    private var cardholderName: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        env = arguments?.get("env") as String
        GPDConfig.setDevEnviroment(GPDEnviroment.valueOf(env!!))
        loadEnvVariables()
        loadingDialog = activity?.let { LoadingDialog(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tds_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnExec.setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()

                cardNumber = edtCardNumberTds.text.toString().replace(Regex("[^\\d]"), "")
                cardExpiration = edtExpirationTds.text.toString()
                paymentMethod = spPaymentMethodTds.selectedItem.toString()
                cardCvv = edtCvvTds.text.toString()
                cardholderName = edtCardholderTds.text.toString()

                getToken()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEnvVariables() {
        val preferences = context?.getSharedPreferences("APP-TEST-SDK", Context.MODE_PRIVATE)

        if(env == GPDEnviroment.HOMOLOG.toString()) {
            sellerId = preferences?.getString("SELLER_ID_HOMOLOG", "").toString()
            clientId = preferences?.getString("CLIENT_ID_HOMOLOG", "").toString()
            clientSecret = preferences?.getString("CLIENT_SECRET_HOMOLOG", "").toString()
        } else {
            sellerId = preferences?.getString("SELLER_ID_PROD", "").toString()
            clientId = preferences?.getString("CLIENT_ID_PROD", "").toString()
            clientSecret = preferences?.getString("CLIENT_SECRET_PROD", "").toString()
        }

        val str = "$clientId:$clientSecret"
        oauth = Base64.getEncoder().encodeToString(str.toByteArray())
    }

    private fun getToken() {
        var baseUrl = ""
        when(env) {
            "HOMOLOG" -> {
                baseUrl = "https://api-homologacao.getnet.com.br/"
            }
            "PROD" -> {
                baseUrl = "https://api.getnet.com.br/"
            }
            "HG" -> {
                baseUrl = "https://api-hg.getnet.com.br:8443/"
            }
        }

        ServiceRepository.getAccessToken(baseUrl, oauth) { accessToken, errorMessage ->
            if(accessToken == "") {
                loadingDialog?.dismissLoadingDialog()
                startResponseFragment(errorMessage)
            } else {
                authKey = accessToken
                executeTdsFlow()
            }
        }
    }

    private fun executeTdsFlow() {
        val tdsInstance = GPDAuthentication3ds.tdsConfigure(requireContext(), env!!)

        generateTokenBuildAndCall { json ->

            when (json.get("response_code")?.asInt) {
                200, 201 -> {
                    val token = json.get("response")?.asJsonObject
                        ?.get("access_token")?.asString

                    GPDAuthentication3ds.tdsInit(
                        generatedTokenResponse = token!!,
                        tdsInstance = tdsInstance,
                        onSuccess = { consumerSessionId ->
                            Log.d(TAG, "executeTdsFlow: tdsInit")
                            tokenizeCardBuildAndCall { json ->

                                when (json.get("response_code")?.asInt) {
                                    200, 201 -> {
                                        val tokenized_card = json.get("response")?.asJsonObject
                                            ?.get("number_token")?.asString

                                        authenticationsBuildAndCall("", token, tokenized_card!!) { json ->
                                            Log.d(TAG, "executeTdsFlow: authenticationsBuildAndCall")

                                            when (json.get("response_code")?.asInt) {
                                                200, 201 -> {

                                                    val status = json?.get("response")?.asJsonObject?.get("status")?.asString

                                                    if(status.equals("PENDING_AUTHENTICATION")) {
                                                        val challenge = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("pareq")?.asString
                                                        val transactionId = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("authentication_transaction_id")?.asString

                                                        tokenizeCardBuildAndCall { json ->
                                                            Log.d(TAG, "executeTdsFlow: tokenizeCardBuildAndCall")

                                                            when (json.get("response_code")?.asInt) {
                                                                200, 201 -> {
                                                                    
                                                                    val tokenized_card = json?.get("response")?.asJsonObject?.get("number_token")?.asString
                                                                    
                                                                    GPDAuthentication3ds.tdsShowChallengeScreen(
                                                                        activity = requireActivity()!!,
                                                                        tdsInstance = tdsInstance,
                                                                        transactionId = transactionId!!,
                                                                        challenge = challenge!!,
                                                                        onSuccess = { _, validateResponse, jwtToken ->
                                                                            Log.d(TAG, "executeTdsFlow: tdsShowChallengeScreen")

                                                                            if (jwtToken != null) {
                                                                                authenticationResultsBuildAndCall(jwtToken, token, tokenized_card!!) { json ->
                                                                                    Log.d(TAG, "executeTdsFlow: authenticationResultsBuildAndCall")

                                                                                    when (json.get("response_code")?.asInt) {
                                                                                        200, 201 -> {
                                                                                            val ucaf = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("ucaf")?.asString
                                                                                            val tdsdsxid = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("directory_server_transaction_id")?.asString
                                                                                            val eci = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("eci")?.asString

                                                                                            tokenizeCardBuildAndCall { json ->
                                                                                                when (json.get(
                                                                                                    "response_code"
                                                                                                )?.asInt) {
                                                                                                    200, 201 -> {
                                                                                                        val tokenized_card = json.get("response")?.asJsonObject
                                                                                                            ?.get("number_token")?.asString

                                                                                                        authenticatedBuildAndCall(tokenized_card!!, ucaf!!, eci!!, tdsdsxid!!) { json ->
                                                                                                            when (json.get("response_code")?.asInt) {
                                                                                                                200, 201 -> {
                                                                                                                    startResponseFragment("3DS Payment Flow Success!")
                                                                                                                }
                                                                                                                else -> startResponseFragment("=====AUTHENTICATED ERROR=====\n${json.toString()}")
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                    else -> startResponseFragment("=====TOKENIZE CARD ERROR=====\n${json.toString()}")
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        else -> startResponseFragment("=====RESULTS ERROR=====\n${json.toString()}")
                                                                                    }
                                                                                }
                                                                            }
                                                                        },
                                                                        onError = { _, validateResponse, jwtToken ->
                                                                            startResponseFragment("cardinalContinue [onError]: ${validateResponse.errorDescription}")
                                                                        },
                                                                        onTimeout = { _, validateResponse, jwtToken ->
                                                                            startResponseFragment("cardinalContinue [onTimeout]: ${validateResponse.errorDescription}")
                                                                        },
                                                                        onCancel = { _, validateResponse, jwtToken ->
                                                                            Log.d(TAG, "cardinalContinue: cancel")
                                                                            loadingDialog?.dismissLoadingDialog()
                                                                        },
                                                                        onFailure = { _, validateResponse, jwtToken ->
                                                                            startResponseFragment("cardinalContinue [onFailure]: ${validateResponse.errorDescription}")
                                                                        }
                                                                    )
                                                                }
                                                                else -> startResponseFragment("=====TOKENIZE CARD ERROR=====\n${json.toString()}")
                                                            }
                                                        }
                                                    } else if (status.equals("AUTHENTICATION_FAILED")) {
                                                        startResponseFragment("authentication failed [onError]: authentication failed")
                                                    } else {
                                                        val ucaf = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("ucaf")?.asString
                                                        val tdsdsxid = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("directory_server_transaction_id")?.asString
                                                        val eci = json.get("response")?.asJsonObject?.get("consumer_authentication_information")?.asJsonObject?.get("eci")?.asString

                                                        tokenizeCardBuildAndCall { json ->
                                                            when (json.get(
                                                                "response_code"
                                                            )?.asInt) {
                                                                200, 201 -> {
                                                                    val tokenized_card = json.get("response")?.asJsonObject
                                                                        ?.get("number_token")?.asString

                                                                    authenticatedBuildAndCall(tokenized_card!!, ucaf!!, eci!!, tdsdsxid!!) { json ->
                                                                        when (json.get("response_code")?.asInt) {
                                                                            200, 201 -> {
                                                                                startResponseFragment("3DS Payment Flow Success!")
                                                                            }
                                                                            else -> startResponseFragment("=====AUTHENTICATED ERROR=====\n${json.toString()}")
                                                                        }
                                                                    }
                                                                }
                                                                else -> startResponseFragment("=====TOKENIZE CARD ERROR=====\n${json.toString()}")
                                                            }
                                                        }
                                                    }
                                                }
                                                else -> startResponseFragment("=====AUTHENTICATIONS ERRO=====\n${json.toString()}")
                                            }
                                        }
                                    }
                                    else -> startResponseFragment("=====TOKENIZE CARD ERROR=====\n${json.toString()}")
                                }
                            }
                        },
                        onError = {p0, p1 ->
                            startResponseFragment("cardinalInit [onError]: ${p0?.errorDescription.toString()} $p1")
                        })
                }
                else -> startResponseFragment("=====GENERATE TOKEN ERROR=====\n${json.toString()}")
            }
        }
    }

    private fun parseItems() : ArrayList<GPDOrderItem3ds>? {
        val objects: ArrayList<GPDOrderItem3ds> = ArrayList()
        val content = "Descricao do item,Nome do item,XXX-00-XXX-00,1,100,100".split("\n")
        for (line in content) {
            val lineContent = line.trim().split(',')

            val description = lineContent[0].trim()
            val name = lineContent[1].trim()
            val sku = lineContent[2].trim()
            val quantity = lineContent[3].trim().toInt()
            val totalAmount = lineContent[4].trim().toInt()
            val unitPrice = lineContent[5].trim().toInt()

            objects.add(GPDOrderItem3ds(description, name, sku, quantity, totalAmount, unitPrice))
        }
        if(objects.isEmpty())
            return null
        return objects
    }

    private fun generateTokenBuildAndCall(callback: (JsonObject) -> Unit) {

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
                totalAmount = 100
            ))
        )

        GPDAuthentication3ds.generateToken(
            authKey = authKey,
            sellerId = sellerId,
            order = generateTokenBody
        ) { response ->
            callback(response)
        }
    }

    private fun authenticationsBuildAndCall(consumerSessionId: String, token: String, tokenized_card: String, callback: (JsonObject) -> Unit) {
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
            totalAmount = 100,
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

    }

    private fun tokenizeCardBuildAndCall(callback: (JsonObject) -> Unit) {
        val cardPayload = TokenPayload(
            cardNumber = cardNumber,
            customerID = "customer"
        )

        GPDAuthentication3ds.getCardNumberTokenized(
            authKey = authKey,
            card = cardPayload
        ) { response ->
            callback(response)
        }
    }

    private fun authenticationResultsBuildAndCall(serverJWT: String, token: String, numberToken: String, callback: (JsonObject) -> Unit) {

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
            totalAmount = 100,
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
    }


    private fun authenticatedBuildAndCall(numberToken: String, ucaf: String, eci: String, tdsdsxid: String, callback: (JsonObject) -> Unit) {
        val card = GPDPaymentAuthenticatedCard(
            numberToken = numberToken,
            brand = "Mastercard",
            securityCode = cardCvv,
            cardholderName = cardholderName,
            expirationYear = cardExpiration.split("/")[1].subSequence(2,4).toString(),
            expirationMonth = cardExpiration.split("/")[0]
        )

        val payment = GPDPaymentAuthenticated(
            orderId = "6d2e4380-d8a3-4ccb-9138-c289182818a3",
            amount = 100,
            currency = "BRL",
            transactionType = "FULL",
            numberInstallments = 1,
            //xid = "stringstringstringstringstringstringstri",
            ucaf = ucaf,
            eci = eci,
            tdsdsxid = tdsdsxid,
            tdsver = "2.1.0",
            paymentMethod = paymentMethod,
            dynamicMcc = "1799",
            softDescriptor = "Descricao para fatura",
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
    }

    private fun  startResponseFragment(response : String) {
        val bundle = Bundle()
        bundle.putString("response", response)
        loadingDialog?.dismissLoadingDialog()
        findNavController().navigate(R.id.action_tdsFlowFormFragment_to_ResponseFragment, bundle)
    }
}