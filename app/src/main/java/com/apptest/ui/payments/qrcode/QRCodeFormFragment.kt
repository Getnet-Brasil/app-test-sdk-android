package com.apptest.ui.payments.qrcode

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.apptest.utils.Helpers
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDDocumentType
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDQrCode
import com.getnetpd.models.client.GPDAddressModel
import com.getnetpd.models.client.GPDCustomerModel
import com.getnetpd.models.client.GPDOrderModel
import com.getnetpd.models.client.GPDShippingModel
import com.getnetpd.models.qrcode.GPDPayments
import com.getnetpd.models.qrcode.GPDQrCodePayment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_qr_code_form.*
import java.util.*

class QRCodeFormFragment : Fragment() {

    private var authKey = ""
    private var arg: Methods? = null
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
        if (arguments != null) {
            arg = arguments?.get("method") as Methods
            Log.d("TAG", "onCreateView: ${arg!!.key}")
        }
        return inflater.inflate(R.layout.fragment_qr_code_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtSellerIdQrCode.text = sellerId
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
            edtPaymentId.setText("")
            edtAmount.setText("")
            edtCurrency.setText("")
            edtOrderId.setText("")
            edtSalesTax.setText("")
            edtCustomerId.setText("")
            edtFirstName.setText("")
            edtLastName.setText("")
            edtName.setText("")
            edtDocumentNumber.setText("")
            edtPhoneNumber.setText("")
            edtEmail.setText("")
            edtStreetBillingAddress.setText("")
            edtNumberBillingAddress.setText("")
            edtComplementBillingAddress.setText("")
            edtDistrictBillingAddress.setText("")
            edtCityBillingAddress.setText("")
            edtStateBillingAddress.setText("")
            edtCountryBillingAddress.setText("")
            edtPostalCodeBillingAddress.setText("")
            edtShippings.setText("")
            edtPayments.setText("")
        }
        btReload.setOnClickListener {
            edtPaymentId.setText("76f7b846-e0b9-4e47-993f-5ecae1a4fbfa")
            edtAmount.setText("100")
            edtCurrency.setText("BRL")
            edtOrderId.setText("6d2e4380-d8a3-4ccb-9138-c289182818a3")
            edtSalesTax.setText("0")
            edtCustomerId.setText("customer_21081826")
            edtFirstName.setText("João")
            edtLastName.setText("da Silva")
            edtName.setText("João")
            edtDocumentNumber.setText("82916868070")
            edtPhoneNumber.setText("5551999887766")
            edtEmail.setText("aceitei@getnet.com.br")
            edtStreetBillingAddress.setText("Av. Brasil")
            edtNumberBillingAddress.setText("1000")
            edtComplementBillingAddress.setText("Sala 1")
            edtDistrictBillingAddress.setText("São Geraldo")
            edtCityBillingAddress.setText("Porto Alegre")
            edtStateBillingAddress.setText("RS")
            edtCountryBillingAddress.setText("Brasil")
            edtPostalCodeBillingAddress.setText("90230060")
            edtShippings.setText("Lucas,Lucas Grando,aceitei@getnet.com.br,51999999999,3000,Av. Brasil,1000,São Geraldo, Porto Alegre, RS, Brasil, 90230060, 12345678")
            edtPayments.setText("CREDIT, false, FULL, 1, LOJA*TESTE*COMPRA-123, 1799, 5551999887766")
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
            R.string.key_generate_qrcode -> {
                containerGenerate.visibility = View.VISIBLE
                containerStatus.visibility = View.GONE
            }
            R.string.key_check_payment_status -> {
                containerGenerate.visibility = View.GONE
                containerStatus.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRequest(key: Int) {
        when (key) {
            R.string.key_check_payment_status -> {
                checkPaymentStatus { startResponseFragment(it.toString()) }
            }
            R.string.key_generate_qrcode -> {
                generateQrCodePayment { startResponseFragment(it.toString()) }
            }
        }
    }

    private fun checkPaymentStatus(callback: (JsonObject) -> Unit) {
        GPDQrCode.checkPaymentStatus(
            authKey = authKey,
            sellerId = sellerId,
            paymentId = edtPaymentId.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun generateQrCodePayment(callback: (JsonObject) -> Unit) {
        val order = GPDOrderModel(
            orderId = edtOrderId.text.toString(),
            salesTax = Helpers.textToIntIfNotEmpty(edtSalesTax.text.toString()),
            productType = spProductType.selectedItem.toString()
        )

        val billingAddress = GPDAddressModel(
            street = edtStreetBillingAddress.text.toString(),
            number = edtNumberBillingAddress.text.toString(),
            complement = edtComplementBillingAddress.text.toString(),
            district = edtDistrictBillingAddress.text.toString(),
            city = edtCityBillingAddress.text.toString(),
            state = edtStateBillingAddress.text.toString(),
            country = edtCountryBillingAddress.text.toString(),
            postalCode = edtPostalCodeBillingAddress.text.toString()
        )

        val customer = GPDCustomerModel(
            customerId = edtCustomerId.text.toString(),
            firstName = edtFirstName.text.toString(),
            lastName = edtLastName.text.toString(),
            name = edtName.text.toString(),
            email = edtEmail.text.toString(),
            documentType = GPDDocumentType.valueOf(spDocumentType.selectedItem.toString()),
            documentNumber = edtDocumentNumber.text.toString(),
            phoneNumber = edtPhoneNumber.text.toString(),
            billingAddress = billingAddress
        )


        var shippings: List<GPDShippingModel>? = null
        var payments: List<GPDPayments>? = null

        try {
            shippings = Helpers.parseShippings(edtShippings.text.toString())
        } catch (e : Exception) {
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "shippings está no formato inválido. Verifique a documentação ou a classe utilizada.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            payments = Helpers.parsePayment(edtPayments.text.toString())
        } catch (e : Exception) {
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "payments está no formato inválido. Verifique a documentação ou a classe utilizada.", Toast.LENGTH_SHORT).show()
            return
        }

        val payment = GPDQrCodePayment(
            amount = Helpers.textToIntIfNotEmpty(edtAmount.text.toString()),
            currency = edtCurrency.text.toString(),
            order = order,
            customer = customer,
            shippings = shippings,
            payments = payments
        )

        if (payment != null) {
            GPDQrCode.qrCodeForPayment(
                authKey = authKey,
                sellerId = sellerId,
                qrCodePayment = payment
            ) { response ->
                callback(response)
            }
        }
    }

    private fun startResponseFragment(response: String) {
        val bundle = Bundle()
        bundle.putString("response", response)
        loadingDialog?.dismissLoadingDialog()
        findNavController().navigate(
            R.id.action_navQrCodeFormFragment_to_ResponseFragment,
            bundle
        )
    }
}