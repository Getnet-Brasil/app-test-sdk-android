package com.apptest.ui.payment3ds

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
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDPayment3ds
import com.getnetpd.models.client.GPDPaymentAuthenticated
import com.getnetpd.models.client.GPDPaymentAuthenticatedCard
import com.getnetpd.models.client.GPDPaymentCancel
import com.getnetpd.models.client.GPDPaymentConfirmation
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_payment3ds_form.*
import java.util.*


class Payment3dsFormFragment : Fragment() {

    private var authKey = ""
    private var tokenNumber = ""
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
            Log.d("TAG", "onCreateView: ${arg!!.key}")
        }
        return inflater.inflate(R.layout.fragment_payment3ds_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdPayment3ds.text = sellerId
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
            edtOrderId2.setText("")
            edtAmount2.setText("")
            edtCurrency4.setText("")
            edtTransactionType.setText("")
            edtNumberInstallments.setText("")
            edtXid.setText("")
            edtUcaf.setText("")
            edtEci.setText("")
            edtTdsdsxid.setText("")
            edtTdsver.setText("")
            edtPaymentMethod.setText("")
            edtDynamicMcc.setText("")
            edtSoftDescriptor2.setText("")
            edtCustomerId5.setText("")
            edtCredentialsOnFileType.setText("")
            edtNumberToken3.setText("")
            edtBrand3ds.setText("")
            edtSecurityCode2.setText("")
            edtExpirationMonth3.setText("")
            edtExpirationYear3.setText("")
            edtCardholderName.setText("")
            edtPaymentId.setText("")
            edtPaymentAmount.setText("")
        }
        btReload.setOnClickListener {
            edtOrderId2.setText("6d2e4380-d8a3-4ccb-9138-c289182818a3")
            edtAmount2.setText("19990")
            edtCurrency4.setText("BRL")
            edtTransactionType.setText("FULL")
            edtNumberInstallments.setText("01")
            edtXid.setText("stringstringstringstringstringstringstri")
            edtUcaf.setText("stringstringstringstringstringstringstri")
            edtEci.setText("stringstringstringstringstringstringstri")
            edtTdsdsxid.setText("dbdcb82d-63c5-496f-ae27-1ecfc3a8dbec")
            edtTdsver.setText("2.1.0")
            edtPaymentMethod.setText("CREDIT")
            edtDynamicMcc.setText("1799")
            edtSoftDescriptor2.setText("Descrição para fatura")
            edtCustomerId5.setText("string")
            edtCredentialsOnFileType.setText("ONE_CLICK")
            edtNumberToken3.setText("dfe05208b105578c070f806c80abd3af09e246827d29b866cf4ce16c205849977c9496cbf0d0234f42339937f327747075f68763537b90b31389e01231d4d13c")
            edtBrand3ds.setText("Mastercard")
            edtSecurityCode2.setText("123")
            edtExpirationMonth3.setText("12")
            edtExpirationYear3.setText("2019")
            edtCardholderName.setText("JOAO DA SILVA")
            edtPaymentId.setText("123456")
            edtPaymentAmount.setText("19990")
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
        when(key) {
            R.string.key_payment_authenticated -> {
                containerPaymentAuthenticated.visibility = View.VISIBLE
                containerManagePayment.visibility = View.GONE
            }
            R.string.key_payment_confirm -> {
                containerPaymentAuthenticated.visibility = View.GONE
                containerManagePayment.visibility = View.VISIBLE
                spPaymentMethodCancel.visibility = View.GONE
                spPaymentMethod2.visibility = View.VISIBLE
            }
            R.string.key_payment_adjustment -> {
                containerPaymentAuthenticated.visibility = View.GONE
                containerManagePayment.visibility = View.VISIBLE
                spPaymentMethodCancel.visibility = View.GONE
                spPaymentMethod2.visibility = View.VISIBLE
            }
            R.string.key_payment_cancel -> {
                containerPaymentAuthenticated.visibility = View.GONE
                containerManagePayment.visibility = View.VISIBLE
                spPaymentMethodCancel.visibility = View.VISIBLE
                spPaymentMethod2.visibility = View.GONE
            }
        }
    }

    private fun selectRequest(key: Int) {
        when(key) {
            R.string.key_payment_authenticated -> {
                requestPaymentAuthenticated {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_confirm -> {
                confirmPayment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_adjustment -> {
                adjustPayment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_payment_cancel -> {
                cancelPayment {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun requestPaymentAuthenticated(callback: (JsonObject) -> Unit) {

        val card = GPDPaymentAuthenticatedCard(
            numberToken = edtNumberToken3.text.toString(),
            brand = edtBrand3ds.text.toString(),
            securityCode = edtSecurityCode2.text.toString(),
            cardholderName = edtCardholderName.text.toString(),
            expirationYear = edtExpirationYear3.text.toString(),
            expirationMonth = edtExpirationMonth3.text.toString()
        )

        val payment = GPDPaymentAuthenticated(
            orderId = edtOrderId2.text.toString(),
            amount = Helpers.textToIntIfNotEmpty(edtAmount2.text.toString()),
            currency = edtCurrency4.text.toString(),
            transactionType = edtTransactionType.text.toString(),
            numberInstallments = Helpers.textToIntIfNotEmpty(edtNumberInstallments.text.toString()),
            xid = edtXid.text.toString(),
            ucaf = edtUcaf.text.toString(),
            eci = edtEci.text.toString(),
            tdsdsxid = edtTdsdsxid.text.toString(),
            tdsver = edtTdsver.text.toString(),
            paymentMethod = edtPaymentMethod.text.toString(),
            dynamicMcc = edtDynamicMcc.text.toString(),
            softDescriptor = edtSoftDescriptor2.text.toString(),
            customerId = edtCustomerId5.text.toString(),
            credentialsOnFileType = edtCredentialsOnFileType.text.toString(),
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

    private fun confirmPayment(callback: (JsonObject) -> Unit) {
        val payment = GPDPaymentConfirmation(
            paymentAmount = Helpers.textToIntIfNotEmpty(edtPaymentAmount.text.toString()),
            paymentMethod = spPaymentMethod2.selectedItem.toString()
        )

        GPDPayment3ds.confirmPaymentAuthenticated(
            authKey = authKey,
            sellerId = sellerId,
            paymentId = edtPaymentId.text.toString(),
            payment = payment
        ) { response ->
            callback(response)
        }
    }

    private fun adjustPayment(callback: (JsonObject) -> Unit) {
        val payment = GPDPaymentConfirmation(
            paymentAmount = Helpers.textToIntIfNotEmpty(edtPaymentAmount.text.toString()),
            paymentMethod = spPaymentMethod2.selectedItem.toString()
        )

        GPDPayment3ds.adjustPaymentAuthenticated(
            authKey = authKey,
            sellerId = sellerId,
            paymentId = edtPaymentId.text.toString(),
            payment = payment
        ) { response ->
            callback(response)
        }
    }

    private fun cancelPayment(callback: (JsonObject) -> Unit) {
        val payment = GPDPaymentCancel(
            paymentMethod = spPaymentMethodCancel.selectedItem.toString()
        )

        GPDPayment3ds.cancelPaymentAuthenticated(
            authKey = authKey,
            sellerId = sellerId,
            paymentId = edtPaymentId.text.toString(),
            payment = payment
        ) { response ->
            callback(response)
        }
    }

    private fun  startResponseFragment(response : String) {
        val bundle = Bundle()
        bundle.putString("response", response)
        loadingDialog?.dismissLoadingDialog()
        findNavController().navigate(R.id.action_payment3dsFormFragment_to_ResponseFragment, bundle)
    }
}