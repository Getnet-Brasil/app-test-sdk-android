package com.apptest.ui.payments.boleto

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
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDDocumentType
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDPaymentBoleto
import com.getnetpd.models.BoletoEncryptPayload
import com.getnetpd.models.client.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_boleto_form.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class BoletoFormFragment : Fragment() {

    companion object {
        private const val TAG = "BoletoFormFragment"
    }

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
        if(arguments != null) {
            arg = arguments?.get("method") as Methods
        }
        return inflater.inflate(R.layout.fragment_boleto_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtSellerIdBoleto.text = sellerId
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
            edtAmountBoleto.setText("")
            edtOrderIdBoleto.setText("")
            edtSalesTaxBoleto.setText("")
            edtFirstNameBoleto.setText("")
            edtNameBoleto.setText("")
            edtDocumentNumberBoleto.setText("")
            edtEmailBoleto.setText("")
            edtOurNumberBoleto.setText("")
            edtExpirationDateBoleto.setText("")
            edtInstructionsBoleto.setText("")
            edtProviderBoleto.setText("")
            edtNameBoleto.setText("")
            edtEmailBoleto.setText("")
            edtDocumentTypeBoleto.setText("")
            edtDocumentNumberCustomerBoleto.setText("")
            edtStreetBoleto.setText("")
            edtNumberBoleto.setText("")
            edtComplementBoleto.setText("")
            edtDistrictBoleto.setText("")
            edtCityBoleto.setText("")
            edtStateBoleto.setText("")
            edtCountryBoleto.setText("")
            edtPostalCodeBoleto.setText("")
            edtFirstNameBoleto.setText("")
            edtPaymentIdBoleto.setText("")
        }
        btReload.setOnClickListener {
            edtAmountBoleto.setText("100")
            edtOrderIdBoleto.setText("6d2e4380-d8a3-4ccb-9138-c289182818a3")
            edtSalesTaxBoleto.setText("0")
            edtFirstNameBoleto.setText("João")
            edtNameBoleto.setText("Joao da Silva")
            edtDocumentNumberBoleto.setText("170500000019763")
            edtEmailBoleto.setText("aceitei@getnet.com.br")
            edtOurNumberBoleto.setText("123456789012")
            edtExpirationDateBoleto.setText("16/11/2023")
            edtInstructionsBoleto.setText("Não receber após o vencimento")
            edtProviderBoleto.setText("santander")
            edtNameBoleto.setText("Joao da Silva")
            edtEmailBoleto.setText("aceitei@getnet.com.br")
            edtDocumentTypeBoleto.setText("CPF")
            edtDocumentNumberCustomerBoleto.setText("51460878060")
            edtStreetBoleto.setText("Av. Brasil")
            edtNumberBoleto.setText("1000")
            edtComplementBoleto.setText("Sala 1")
            edtDistrictBoleto.setText("São Geraldo")
            edtCityBoleto.setText("Porto Alegre")
            edtStateBoleto.setText("RS")
            edtCountryBoleto.setText("Brasil")
            edtPostalCodeBoleto.setText("90230060")
            edtFirstNameBoleto.setText("João")
            edtPaymentIdBoleto.setText("61b28adaa498e40011da8126")
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

    private fun selectViewToShow(key : Int) {
        when (key) {
            R.string.key_create_boleto_v1 -> {
                containerBoleto.visibility = View.VISIBLE
                containerDownload.visibility = View.GONE
            }
            R.string.key_download_boleto_pdf_v1 -> {
                containerBoleto.visibility = View.GONE
                containerDownload.visibility = View.VISIBLE
            }
            R.string.key_download_boleto_html_v1 -> {
                containerBoleto.visibility = View.GONE
                containerDownload.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRequest(key: Int) {
        when(key) {
            R.string.key_create_boleto_v1 -> {
                boletoPayment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_download_boleto_pdf_v1 -> {
                getPdf {
                    val contentType = it?.headers()?.get("content-type")

                    if(contentType == "application/octet-stream") {
                        val stream = streamToString(it?.body())
                        startResponseFragment(stream)
                    }
                    else {
                        val json = it?.errorBody()?.string()
                        if (json != null) {
                            startResponseFragment(json)
                        }
                    }
                }
            }

            R.string.key_download_boleto_html_v1 -> {
                getHTML {
                    val contentType = it?.headers()?.get("content-type")

                    if(contentType == "text/html;charset=utf-8") {
                        val stream = streamToString(it?.body())
                        startResponseFragment(stream)
                    }
                    else {
                        val json = it?.errorBody()?.string()
                        if (json != null) {
                            startResponseFragment(json)
                        }
                    }
                }
            }
        }
    }

    private fun streamToString(responseBody: ResponseBody?): String {
        val inputStream = responseBody?.byteStream()
        val isReader = InputStreamReader(inputStream)
        val reader = BufferedReader(isReader)
        val sb = StringBuffer()
        var str: String?
        while (reader.readLine().also { str = it } != null) {
            sb.append(str)
        }
        return sb.toString()
    }

    private fun boletoPayment(callback: (JsonObject) -> Unit) {
        setupBoleto {
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
                sellerId =sellerId,
                authKey = authKey,
                payment = boletoPayload
            ) { response ->
                callback(response)
            }
        }
    }

    private fun getPdf(callback: (Response<ResponseBody>?) -> Unit) {
        GPDPaymentBoleto.getPdfV1(
            paymentId = edtPaymentIdBoleto.text.toString(),
        ) { response ->
            callback(response)
        }
    }

    private fun getHTML(callback: (Response<ResponseBody>?) -> Unit) {
        GPDPaymentBoleto.getHtmlV1(
            paymentId = edtPaymentIdBoleto.text.toString(),
        ) { response ->
            callback(response)
        }
    }

    private fun setupBoleto(callback: (String) -> Unit) {
        val boletoEncryptPayload = BoletoEncryptPayload(sellerId, "BRL")
        GPDPaymentBoleto.getBoletoEncrypt(
            boletoEncryptPayload,
            { boletoEncoded ->
                boletoEncoded?.let {
                    callback(boletoEncoded)
                }
            },
            { json ->
                startResponseFragment("response: $json")
            }
        )
    }

    private fun  startResponseFragment(response : String) {
        val bundle = Bundle()
        bundle.putString("response", response)
        loadingDialog?.dismissLoadingDialog()
        findNavController().navigate(R.id.action_navBoletoFormFragment_to_ResponseFragment, bundle)
    }
}