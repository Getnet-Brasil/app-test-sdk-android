package com.apptest.ui.payments.credit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apptest.R
import com.apptest.model.Methods
import com.apptest.services.ServiceRepository
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDDocumentType
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.enums.client.GPDTransactionType
import com.getnetpd.managers.GPDCard
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDPaymentCredit
import com.getnetpd.models.client.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_credit_form.*
import java.util.*

class CreditFormFragment : Fragment() {
    private var authKey = ""
    private var arg: Methods? = null
    private var env: String? = null
    private var loadingDialog: LoadingDialog? = null

    private var sellerId: String = ""
    private var clientId: String = ""
    private var clientSecret: String = ""
    private var oauth: String = ""

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
        if(arguments != null) {
            arg = arguments?.get("method") as Methods
        }
        return inflater.inflate(R.layout.fragment_credit_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdPaymentCredit.text = sellerId
        arg?.let { selectViewToShow(it.key) }
        btRequest.setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()
                getToken()
            }
        }
        btClearRequest.setOnClickListener {
            edtAmountPaymentCredit.setText("")
            edtCurrencyPaymentCredit.setText("")
            edtOrderIdPaymentCredit.setText("")
            edtSalesTaxPaymentCredit.setText("")
            edtCustomerIdPaymentCredit.setText("")
            edtFirstNamePaymentCredit.setText("")
            edtLastNamePaymentCredit.setText("")
            edtNamePaymentCredit.setText("")
            edtDocumentNumberPaymentCredit.setText("")
            edtPhoneNumberPaymentCredit.setText("")
            edtEmailPaymentCredit.setText("")
            edtStreetBillingAddressPaymentCredit.setText("")
            edtNumberBillingAddressPaymentCredit.setText("")
            edtComplementBillingAddressPaymentCredit.setText("")
            edtDistrictBillingAddressPaymentCredit.setText("")
            edtCityBillingAddressPaymentCredit.setText("")
            edtStateBillingAddressPaymentCredit.setText("")
            edtCountryBillingAddressPaymentCredit.setText("")
            edtPostalCodeBillingAddressPaymentCredit.setText("")
            edtShippingsPaymentCredit.setText("")
            edtIdentificationCodePaymentCredit.setText("")
            edtDocumentTypeSubMerchantPaymentCredit.setText("")
            edtDocumentNumberSubMerchantPaymentCredit.setText("")
            edtAddressSubMerchantPaymentCredit.setText("")
            edtCitySubMerchantPaymentCredit.setText("")
            edtStateSubMerchantPaymentCredit.setText("")
            edtPostalCodeSubMerchantPaymentCredit.setText("")
            edtDelayedPaymentCredit.setText("")
            edtPreAuthPaymentCredit.setText("")
            edtSaveCardDataPaymentCredit.setText("")
            edtTransactionTypePaymentCredit.setText("")
            edtNumberInstallmentsPaymentCredit.setText("")
            edtSoftDescriptorPaymentCredit.setText("")
            edtDynamicMccPaymentCredit.setText("")
            edtCardNumberToken.setText("")
            edtCardBrand.setText("")
            edtCardCardholderName.setText("")
            edtCardExpirationMonth.setText("")
            edtCardExpirationYear.setText("")
            edtSecurityCodeCredit.setText("")
            edtPaymentIdCredit.setText("")
            edtAmountCreditManage.setText("")
            edtCurrencyPaymentCreditManage.setText("")
            edtSoftDescriptorPaymentCreditManage.setText("")
            edtDynamicMccPaymentCreditManage.setText("")
        }
        btReload.setOnClickListener {
            edtAmountPaymentCredit.setText("100")
            edtCurrencyPaymentCredit.setText("BRL")
            edtOrderIdPaymentCredit.setText("6d2e4380-d8a3-4ccb-9138-c289182818a3")
            edtSalesTaxPaymentCredit.setText("0")
            edtCustomerIdPaymentCredit.setText("customer_21081826")
            edtFirstNamePaymentCredit.setText("João")
            edtLastNamePaymentCredit.setText("da Silva")
            edtNamePaymentCredit.setText("João")
            edtDocumentNumberPaymentCredit.setText("82916868070")
            edtPhoneNumberPaymentCredit.setText("5551999887766")
            edtEmailPaymentCredit.setText("aceitei@getnet.com.br")
            edtStreetBillingAddressPaymentCredit.setText("Av. Brasil")
            edtNumberBillingAddressPaymentCredit.setText("1000")
            edtComplementBillingAddressPaymentCredit.setText("Sala 1")
            edtDistrictBillingAddressPaymentCredit.setText("São Geraldo")
            edtCityBillingAddressPaymentCredit.setText("Porto Alegre")
            edtStateBillingAddressPaymentCredit.setText("RS")
            edtCountryBillingAddressPaymentCredit.setText("Brasil")
            edtPostalCodeBillingAddressPaymentCredit.setText("90230060")
            edtShippingsPaymentCredit.setText("Lucas,Lucas Grando,aceitei@getnet.com.br,51999999999,3000,Av. Brasil,1000,São Geraldo, Porto Alegre, RS, Brasil, 90230060, 12345678")
            edtIdentificationCodePaymentCredit.setText("9058344")
            edtDocumentTypeSubMerchantPaymentCredit.setText("CNPJ")
            edtDocumentNumberSubMerchantPaymentCredit.setText("20551625000159")
            edtAddressSubMerchantPaymentCredit.setText("Torre Negra 44")
            edtCitySubMerchantPaymentCredit.setText("Cidade")
            edtStateSubMerchantPaymentCredit.setText("RS")
            edtPostalCodeSubMerchantPaymentCredit.setText("90520000")
            edtDelayedPaymentCredit.setText("false")
            edtPreAuthPaymentCredit.setText("false")
            edtSaveCardDataPaymentCredit.setText("false")
            edtTransactionTypePaymentCredit.setText("FULL")
            edtNumberInstallmentsPaymentCredit.setText("1")
            edtSoftDescriptorPaymentCredit.setText("LOJA*TESTE*COMPRA-123")
            edtDynamicMccPaymentCredit.setText("1799")
            edtCardNumberToken.setText("dfe05208b105578c070f806c80abd3af09e246827d29b866cf4ce16c205849977c9496cbf0d0234f42339937f327747075f68763537b90b31389e01231d4d13c")
            edtCardBrand.setText("Mastercard")
            edtCardCardholderName.setText("JOAO DA SILVA")
            edtCardExpirationMonth.setText("12")
            edtCardExpirationYear.setText("23")
            edtSecurityCodeCredit.setText("123")
            edtPaymentIdCredit.setText("b71af724-7c98-44a0-829e-7d5f4e4e9cbf")
            edtAmountCreditManage.setText("19990")
            edtCurrencyPaymentCreditManage.setText("BRL")
            edtSoftDescriptorPaymentCreditManage.setText("LOJA*TESTE*COMPRA-123")
            edtDynamicMccPaymentCreditManage.setText("1799")
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
                arg?.let { method -> selectRequest(method.key) }
            }
        }
    }

    private fun selectViewToShow(arg : Int) {
        when(arg) {
            R.string.key_payment_credit_verify_card -> {
                containerPayment.visibility = View.GONE
                containerVerifyCard.visibility = View.VISIBLE
                containerManagerPayment.visibility = View.GONE
            }
            R.string.key_payment_credit -> {
                containerPayment.visibility = View.VISIBLE
                containerVerifyCard.visibility = View.VISIBLE
                containerManagerPayment.visibility = View.GONE
            }
            R.string.key_payment_credit_confirmation -> {
                containerPayment.visibility = View.GONE
                containerVerifyCard.visibility = View.GONE
                containerManagerPayment.visibility = View.VISIBLE
                containerAdjust.visibility = View.GONE
                tvAmountCreditManage.visibility = View.VISIBLE
                edtAmountCreditManage.visibility = View.VISIBLE
            }
            R.string.key_payment_credit_adjust -> {
                containerPayment.visibility = View.GONE
                containerVerifyCard.visibility = View.GONE
                containerManagerPayment.visibility = View.VISIBLE
                containerAdjust.visibility = View.VISIBLE
            }
            R.string.key_payment_credit_cancel -> {
                containerPayment.visibility = View.GONE
                containerVerifyCard.visibility = View.GONE
                containerManagerPayment.visibility = View.VISIBLE
                containerAdjust.visibility = View.GONE
                tvAmountCreditManage.visibility = View.GONE
                edtAmountCreditManage.visibility = View.GONE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_payment_credit_verify_card -> {
                verifyCard {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_credit -> {
                payment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_credit_confirmation -> {
                paymentConfirmation {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_credit_adjust -> {
                paymentAdjust {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_credit_cancel -> {
                paymentCancel {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun verifyCard(callback: (JsonObject) -> Unit) {
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
            callback(response)
        }
    }

    private fun payment(callback: (JsonObject) -> Unit) {

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
            callback(response)
        }
    }

    private fun paymentConfirmation(callback: (JsonObject) -> Unit) {

        GPDPaymentCredit.confirm(
            authKey = authKey,
            paymentId = edtPaymentIdCredit.text.toString(),
            amount = edtAmountCreditManage.text.toString().toInt()
        ) { response ->
            callback(response)
        }
    }

    private fun paymentAdjust(callback: (JsonObject) -> Unit) {

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
            callback(response)
        }
    }

    private fun paymentCancel(callback: (JsonObject) -> Unit) {

        GPDPaymentCredit.cancel(
            authKey = authKey,
            paymentId = edtPaymentIdCredit.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_creditFormFragment_to_ResponseFragment, bundle)
    }
}