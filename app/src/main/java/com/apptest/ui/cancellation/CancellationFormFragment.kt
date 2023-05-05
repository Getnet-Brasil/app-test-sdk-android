package com.apptest.ui.cancellation

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
import com.getnetpd.managers.GPDPaymentCancel
import com.getnetpd.models.client.GPDCancelModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_cancellation_form.*
import java.util.*

class CancellationFormFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_cancellation_form, container, false)
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
            edtPaymentIdCancellation.setText("")
            edtCancelAmountCancellation.setText("")
            edtCancelCustomKey.setText("")
            edtCancelCustomKey2.setText("")
            edtCancelRequestId.setText("")
        }
        btReload.setOnClickListener {
            edtPaymentIdCancellation.setText("06f256c8-1bbf-42bf-93b4-ce2041bfb87e")
            edtCancelAmountCancellation.setText("19990")
            edtCancelCustomKey.setText("4ec33ee18f9e45bfb73c5c30667f9006")
            edtCancelCustomKey2.setText("4ec33ee18f9e45bfb73c5c30667f9006")
            edtCancelRequestId.setText("20171117084237501")
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
            R.string.key_cancellation_request -> {
                containerCancellationRequest.visibility = View.VISIBLE
                containerGetByCustomer.visibility = View.GONE
                containerGetById.visibility = View.GONE
            }
            R.string.key_get_cancellation_by_customer -> {
                containerCancellationRequest.visibility = View.GONE
                containerGetByCustomer.visibility = View.VISIBLE
                containerGetById.visibility = View.GONE
            }
            R.string.key_get_cancellation_by_id -> {
                containerCancellationRequest.visibility = View.GONE
                containerGetByCustomer.visibility = View.GONE
                containerGetById.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_cancellation_request -> {
                request {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_cancellation_by_customer -> {
                getByCustomer {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_cancellation_by_id -> {
                getById {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun request(callback: (JsonObject) -> Unit) {
        val cancel = GPDCancelModel(
            paymentId = edtPaymentIdCancellation.text.toString(),
            cancelAmount = edtCancelAmountCancellation.text.toString().toInt(),
            cancelCustomKey = edtCancelCustomKey.text.toString()
        )
        
        GPDPaymentCancel.cancel(
            authKey = authKey,
            sellerId = sellerId,
            cancel = cancel
        ) {
            startResponseFragment(it.toString())
        }
    }

    private fun getByCustomer(callback: (JsonObject) -> Unit) {
        GPDPaymentCancel.getByKey(
            authKey = authKey,
            sellerId = sellerId,
            cancelCustomKey = edtCancelCustomKey2.text.toString()
        ) {
            startResponseFragment(it.toString())
        }
    }

    private fun getById(callback: (JsonObject) -> Unit) {
        GPDPaymentCancel.getById(
            authKey = authKey,
            sellerId = sellerId,
            cancelRequestId = edtCancelRequestId.text.toString()
        ) {
            startResponseFragment(it.toString())
        }
    }

    private fun startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_cancellationFormFragment_to_ResponseFragment, bundle)
    }
}