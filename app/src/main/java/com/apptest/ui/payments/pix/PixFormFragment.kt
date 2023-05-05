package com.apptest.ui.payments.pix

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
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDQrCode
import com.getnetpd.models.qrcode.GPDQrCodeForPix
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_pix_form.*
import java.util.*

class PixFormFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_pix_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdCustomer.text = sellerId
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
            edtAmountPix.setText("")
            edtCurrencyPix.setText("")
            edtOrderIdPix.setText("")
            edtCustomerIdPix.setText("")
            edtPaymentIdPix.setText("")
        }
        btReload.setOnClickListener {
            edtAmountPix.setText("100")
            edtCurrencyPix.setText("BRL")
            edtOrderIdPix.setText("DEV-1608748980")
            edtCustomerIdPix.setText("string")
            edtPaymentIdPix.setText("100")
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
            R.string.key_generate_pix -> {
                containerGeneratePix.visibility = View.VISIBLE
                containerStatusPix.visibility = View.GONE
            }
            R.string.key_check_payment_pix_status -> {
                containerGeneratePix.visibility = View.GONE
                containerStatusPix.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_generate_pix -> {
                generatePixQrCode {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_check_payment_pix_status -> {
                checkPaymentPixStatus {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun generatePixQrCode(callback: (JsonObject) -> Unit) {
        val pix = GPDQrCodeForPix(
            amount = edtAmountPix.text.toString().toInt(),
            currency = edtCurrencyPix.text.toString(),
            orderId = edtOrderIdPix.text.toString(),
            customerId = edtCurrencyPix.text.toString()
        )

        GPDQrCode.generateQrCodeForPix(
            authKey = authKey,
            sellerId = sellerId,
            generateQrCodeForPix = pix
        ) { response ->
            callback(response)
        }
    }

    private fun checkPaymentPixStatus(callback: (JsonObject) -> Unit) {
        GPDQrCode.checkPaymentPixStatus(
            authKey = authKey,
            sellerId =  sellerId,
            paymentId = edtPaymentIdPix.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_pixFormFragment_to_ResponseFragment, bundle)
    }
}