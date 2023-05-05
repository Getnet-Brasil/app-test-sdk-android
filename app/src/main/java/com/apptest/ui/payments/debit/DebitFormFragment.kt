package com.apptest.ui.payments.debit

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
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDPaymentDebit
import com.getnetpd.models.client.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_debit_form.*
import java.util.*

class DebitFormFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_debit_form, container, false)
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
            edtSoftDescriptorPaymentCredit.setText("")
            edtDynamicMccPaymentCredit.setText("")
            edtCardNumberToken.setText("")
            edtCardBrand.setText("")
            edtCardCardholderName.setText("")
            edtCardExpirationMonth.setText("")
            edtCardExpirationYear.setText("")
            edtSecurityCodeCredit.setText("")
            edtCardholderMobile.setText("")
            edtAuthenticatedDebit.setText("")
            edtPaymentIdDebit.setText("")
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
            edtSoftDescriptorPaymentCredit.setText("LOJA*TESTE*COMPRA-123")
            edtDynamicMccPaymentCredit.setText("1799")
            edtCardNumberToken.setText("dfe05208b105578c070f806c80abd3af09e246827d29b866cf4ce16c205849977c9496cbf0d0234f42339937f327747075f68763537b90b31389e01231d4d13c")
            edtCardBrand.setText("Mastercard")
            edtCardCardholderName.setText("JOAO DA SILVA")
            edtCardExpirationMonth.setText("12")
            edtCardExpirationYear.setText("23")
            edtSecurityCodeCredit.setText("123")
            edtCardholderMobile.setText("5551999887766")
            edtAuthenticatedDebit.setText("false")
            edtPaymentIdDebit.setText("06f256c8-1bbf-42bf-93b4-ce2041bfb87e")
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
            R.string.key_payment_debit -> {
                containerPayment.visibility = View.VISIBLE
                containerCancel.visibility = View.GONE
            }
            R.string.key_payment_debit_cancel -> {
                containerPayment.visibility = View.GONE
                containerCancel.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_payment_debit -> {
                payment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_debit_cancel -> {
                paymentCancel {
                    startResponseFragment(it.toString())
                }
            }
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
            callback(response)
        }
    }

    private fun paymentCancel(callback: (JsonObject) -> Unit) {
        GPDPaymentDebit.cancel(
            authKey = authKey,
            paymentId = edtPaymentIdDebit.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_debitFormFragment_to_ResponseFragment, bundle)
    }
}