package com.apptest.ui.checkout

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.apptest.R
import com.apptest.services.ServiceRepository
import com.apptest.utils.Helpers
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDDocumentType
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.enums.client.GPDTransactionType
import com.getnetpd.interfaces.GPDCallback
import com.getnetpd.managers.GPDConfig
import com.getnetpd.models.client.*
import com.getnetpd.ui.GPDCheckoutActivity
import com.getnetpd.ui.GPDVaultActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_checkout_form.*
import java.util.*
import kotlin.collections.ArrayList


class CheckoutFormFragment : Fragment(), GPDCallback {
    private var authKey = ""
    private var loadingDialog: LoadingDialog? = null
    private var env: String? = null

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
        return inflater.inflate(R.layout.fragment_checkout_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdCheckout.text = sellerId
        cbBoleto.setOnClickListener {
            if(cbBoleto.isChecked)
                llBoletoOptions.visibility = View.VISIBLE
            else
                llBoletoOptions.visibility = View.GONE
        }
        cbCredit.setOnClickListener {
            listenCreditCheckBox()
        }

        cbCreditAuthenticated.setOnClickListener {
            listenCreditCheckBox()
        }

        btCallCheckout.setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()
                getToken()
            }
        }
        btClearCheckout.setOnClickListener {
            edtCustomerIdCheckout.setText("")
            edtFirstNameCheckout.setText("")
            edtLastNameCheckout.setText("")
            edtNameCheckout.setText("")
            edtDocumentNumberCheckout.setText("")
            edtPhoneNumberCheckout.setText("")
            edtEmailCheckout.setText("")
            edtStreetBillingAddressCheckout.setText("")
            edtNumberBillingAddressCheckout.setText("")
            edtComplementBillingAddressCheckout.setText("")
            edtDistrictBillingAddressCheckout.setText("")
            edtCityBillingAddressCheckout.setText("")
            edtStateBillingAddressCheckout.setText("")
            edtCountryBillingAddressCheckout.setText("")
            edtPostalCodeBillingAddressCheckout.setText("")
            edtAmountCheckout.setText("")
            edtOrderIdCheckout.setText("")
            edtSoftDescriptorCheckout.setText("")
            edtDynamicMccCheckout.setText("")
            cbCredit.isChecked = false
            cbDebit.isChecked = false
            cbBoleto.isChecked = false
            cbCreditAuthenticated.isChecked = false
            cbDebitAuthenticated.isChecked = false
            cbPix.isChecked = false
            cbDebitCaixa.isChecked = false
            cbQrcode.isChecked = false
            edtBoletoOurNumber.setText("")
            edtDocumentNumberBoleto.setText("")
            edtExpirationDateBoleto.setText("")
            edtInstructions.setText("")
            edtNumberInstallmentsCredit.setText("")
            edtShippingsCheckout.setText("")
            edtItemsCheckout.setText("")
            edtAdditionalObjectCheckout.setText("")
            edtCheckoutCurrency.setText("")
            edtCheckoutIpAddress.setText("")
            edtCheckoutDeviceId.setText("")
            edtCheckoutCardholder.setText("")
            llBoletoOptions.visibility = View.GONE
            llCreditOptions.visibility = View.GONE
        }
        btReload.setOnClickListener {
            edtCustomerIdCheckout.setText("customer_21081826")
            edtFirstNameCheckout.setText("João")
            edtLastNameCheckout.setText("da Silva")
            edtNameCheckout.setText("João da Silva")
            edtDocumentNumberCheckout.setText("82916868070")
            edtPhoneNumberCheckout.setText("5551999887766")
            edtEmailCheckout.setText("aceitei@getnet.com.br")
            edtStreetBillingAddressCheckout.setText("Av. Brasil")
            edtNumberBillingAddressCheckout.setText("1000")
            edtComplementBillingAddressCheckout.setText("Sala 1")
            edtDistrictBillingAddressCheckout.setText("São Geraldo")
            edtCityBillingAddressCheckout.setText("Porto Alegre")
            edtStateBillingAddressCheckout.setText("RS")
            edtCountryBillingAddressCheckout.setText("Brasil")
            edtPostalCodeBillingAddressCheckout.setText("90230060")
            edtAmountCheckout.setText("100")
            edtOrderIdCheckout.setText("54321")
            edtSoftDescriptorCheckout.setText("LOJA*TESTE*COMPRA-123")
            edtDynamicMccCheckout.setText("1799")
            cbCredit.isChecked = true
            cbDebit.isChecked = true
            cbBoleto.isChecked = true
            cbCreditAuthenticated.isChecked = true
            cbDebitAuthenticated.isChecked = true
            cbPix.isChecked = true
            cbDebitCaixa.isChecked = true
            cbQrcode.isChecked = true
            edtBoletoOurNumber.setText("123456789012")
            edtDocumentNumberBoleto.setText("170500000019763")
            edtExpirationDateBoleto.setText("16/11/2023")
            edtInstructions.setText("Não receber após o vencimento")
            edtNumberInstallmentsCredit.setText("1")
            edtShippingsCheckout.setText("Lucas,Lucas Grando,aceitei@getnet.com.br,51999999999,3000,Av. Brasil,1000,Complement,São Geraldo, Porto Alegre, RS, Brasil, 90230060,lowcost,02")
            edtItemsCheckout.setText("Item,Descrição,100,2,1,sku,200")
            edtAdditionalObjectCheckout.setText("{antifraud:{device:{ip_address:127.0.0.1,device_id:hash-device-id}}}")
            edtCheckoutCurrency.setText("BRL")
            edtCheckoutIpAddress.setText("127.0.0.1")
            edtCheckoutDeviceId.setText("hash-device-id")
            edtCheckoutCardholder.setText("5551999887766")
            llBoletoOptions.visibility = View.VISIBLE
            llCreditOptions.visibility = View.VISIBLE
        }
    }

    fun listenCreditCheckBox() {
        if(cbCredit.isChecked || cbCreditAuthenticated.isChecked)
            llCreditOptions.visibility = View.VISIBLE
        else
            llCreditOptions.visibility = View.GONE
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            } else {
                authKey = accessToken
                checkout()
            }
        }
    }

    private fun parseShippings(): List<GPDShippingModel>? {
        if (edtShippingsCheckout.text.toString() == "")
            return null

        val objects: ArrayList<GPDShippingModel> = ArrayList()
        val content = edtShippingsCheckout.text.toString().split("\n")
        for (line in content) {
            val lineContent = line.trim().split(',')
            val address = GPDAddressModel(
                street = lineContent[5].trim(),
                number = lineContent[6].trim(),
                complement = lineContent[7].trim(),
                district = lineContent[8].trim(),
                city = lineContent[9].trim(),
                state = lineContent[10].trim(),
                country = lineContent[11].trim(),
                postalCode = lineContent[12].trim()
            )
            objects.add(
                GPDShippingModel(
                    firstName = lineContent[0].trim(),
                    name = lineContent[1].trim(),
                    email = lineContent[2].trim(),
                    phoneNumber = lineContent[3].trim(),
                    shippingAmount = lineContent[4].trim().toInt(),
                    address
                )
            )
        }

        if (objects.isEmpty())
            return null
        return objects
    }

    private fun parseItems(): List<GPDItemModel>? {
        if (edtItemsCheckout.text.toString() == "")
            return null

        val objects: ArrayList<GPDItemModel> = ArrayList()
        val content = edtItemsCheckout.text.toString().split("\n")
        for (line in content) {
            val lineContent = line.trim().split(',')
            objects.add(
                GPDItemModel(
                    name = lineContent[0].trim(),
                    description = lineContent[1].trim(),
                    value = lineContent[2].trim().toInt(),
                    quantity = lineContent[3].trim().toInt(),
                    unitPrice = lineContent[4].trim(),
                    sku = lineContent[5].trim(),
                    totalAmount = lineContent[6].trim()
                )
            )
        }

        if (objects.isEmpty())
            return null
        return objects
    }

    private fun checkout() {
        val amount = Helpers.strToInt(edtAmountCheckout.text.toString())

        val address = GPDAddressModel(
            street = edtStreetBillingAddressCheckout.text.toString(),
            number = edtNumberBillingAddressCheckout.text.toString(),
            district = edtDistrictBillingAddressCheckout.text.toString(),
            city = edtCityBillingAddressCheckout.text.toString(),
            state = edtStateBillingAddressCheckout.text.toString(),
            country = edtCountryBillingAddressCheckout.text.toString(),
            postalCode = edtPostalCodeBillingAddressCheckout.text.toString(),
            complement = edtComplementBillingAddressCheckout.text.toString()
        )

        val customer = GPDCustomerModel(
            customerId = edtCustomerIdCheckout.text.toString(),
            firstName = edtFirstNameCheckout.text.toString(),
            lastName = edtLastNameCheckout.text.toString(),
            name = edtNameCheckout.text.toString(),
            email = edtEmailCheckout.text.toString(),
            documentType = GPDDocumentType.valueOf(spDocumentTypeCheckout.selectedItem.toString()),
            documentNumber = edtDocumentNumberCheckout.text.toString(),
            phoneNumber = edtPhoneNumberCheckout.text.toString(),
            billingAddress = address
        )

        var shippings: List<GPDShippingModel>? = null
        var items: List<GPDItemModel>? = null
        try {
            shippings = parseShippings()
        } catch (e : Exception) {
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "shippings está no formato inválido. Verifique a documentação ou a classe utilizada.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            items = parseItems()
        } catch (e : Exception) {
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "items está no formato inválido. Verifique a documentação ou a classe utilizada.", Toast.LENGTH_SHORT).show()
            return
        }

        val boleto = if(cbBoleto.isChecked)
            GPDCheckoutBoletoModel(
                ourNumber = edtBoletoOurNumber.text.toString(),
                documentNumber = edtDocumentNumberCheckout.text.toString(),
                expirationDate = edtExpirationDateBoleto.text.toString(),
                instructions = edtInstructions.text.toString()
            ) else null

        val additionalObject = edtAdditionalObjectCheckout.text.toString()

        val qrCodeModel = (if (edtNumberInstallmentsCredit.text.isNotBlank()) Integer.parseInt(edtNumberInstallmentsCredit.text.toString()) else null)?.let {
            (if(edtDynamicMccCheckout.text.isNotBlank()) Integer.parseInt(edtDynamicMccCheckout.text.toString()) else null)?.let { it1 ->
                GPDCheckoutQRCodeModel(
                    device = GPDDeviceModel(
                        ipAddress = edtCheckoutIpAddress.text.toString(),
                        deviceId = edtCheckoutDeviceId.text.toString()
                    ),
                    transactionType = GPDTransactionType.FULL,
                    numberInstallments = it,
                    softDescriptor = edtSoftDescriptorCheckout.text.toString(),
                    dynamicMcc = it1,
                    payments = edtCheckoutCardholder.text.toString()
                )
            }
        }

        val authentication = GPDAuthentication(
            acsWindowSize = edtAcsWindowSize.text.toString(),
            npaCode = edtNpaCode.text.toString(),
            challengeCode = edtChallengeCode.text.toString(),
            installmentTotalCount = edtInstallmentTotalCount.text.toString().toInt(),
            messageCategory = edtMessageCategory.text.toString(),
            transactionMode = edtTransactionMode.text.toString()
        )

        val gpdPaymentLayoutModel = GPDCheckoutModel(
            amount = amount,
            orderId = edtOrderIdCheckout.text.toString(),
            sellerId = sellerId,
            shippings = shippings,
            items = items,
            customer = customer,
            boleto = boleto,
            credit = if (cbCredit.isChecked || cbCreditAuthenticated.isChecked)
                GPDCheckoutCreditModel(
                    numberInstallments = if (Helpers.checkInputIsNotEmpty(edtNumberInstallmentsCredit.text.toString())) edtNumberInstallmentsCredit.text.toString().toInt() else 1,
                    authenticated = cbCreditAuthenticated.isChecked,
                    notAuthenticated = cbCredit.isChecked
                ) else null,
            debit = if (cbDebit.isChecked || cbDebitAuthenticated.isChecked)
                GPDCheckoutDebitModel(
                    authenticated = cbDebitAuthenticated.isChecked,
                    cardCaixa = cbDebitCaixa.isChecked,
                    notAuthenticated = cbDebit.isChecked,
                ) else null,
            debitCaixa = if (cbDebitCaixa.isChecked)
                GPDCheckoutDebitCaixaModel() else null,
            pix = if (cbPix.isChecked) GPDCheckoutPixModel() else null,
            qrCode = if (cbQrcode.isChecked) qrCodeModel else null,
            softDescriptor = edtSoftDescriptorCheckout.text.toString(),
            dynamicMcc = Helpers.strToInt(edtDynamicMccCheckout.text.toString()),
            currency = edtCheckoutCurrency.text.toString(),
            additionalObject = additionalObject,
            authentication = authentication,
            environment = env!!
        )

        loadingDialog?.dismissLoadingDialog()
        startActivity(
            activity?.let {
                GPDCheckoutActivity.create(
                    it.applicationContext,
                    authKey,
                    sellerId,
                    gpdPaymentLayoutModel,
                    true,
                    true
                )
            }
        )
        GPDCheckoutActivity.gpdCallback = this
    }

    override fun onUnauthorizedResponse(activity: AppCompatActivity) {
        if (activity is GPDCheckoutActivity) {
            GPDCheckoutActivity.requestFinish.call()
        }
        if (activity is GPDVaultActivity) {
            GPDVaultActivity.requestFinish.call()
        }
    }

    override fun closedWithSuccess(details: JsonObject?) {
        Log.d("TAG", "JSON de retorno do checkout: $details ")
    }

    override fun closedWithError(details: JsonObject?) {
        Toast.makeText(activity, "${details.toString()}", Toast.LENGTH_LONG).show()
        Log.d("=====", "Closed with error [$details]")
    }

    override fun closedByUser() {
        Log.d("=====", "Closed by user")
    }

}