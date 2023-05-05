package com.apptest.ui.vault

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
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.interfaces.GPDCallback
import com.getnetpd.managers.GPDConfig
import com.getnetpd.ui.GPDCheckoutActivity
import com.getnetpd.ui.GPDVaultActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_vault_form.*
import kotlinx.android.synthetic.main.fragment_vault_form.btReload
import java.util.*

class VaultFormFragment : Fragment(), GPDCallback {
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
        return inflater.inflate(R.layout.fragment_vault_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdVault.text = sellerId
        btCallVault.setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()
                getToken()
            }
        }
        btClearVault.setOnClickListener {
            edtCustomerIdVault.setText("")
            edtCardholderIdentification.setText("")
        }
        btReload.setOnClickListener {
            edtCustomerIdVault.setText("customer_21081826")
            edtCardholderIdentification.setText("Cardholder")
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            } else {
                authKey = accessToken
                addCardScreen()
            }
        }
    }

    private fun addCardScreen() {
        loadingDialog?.dismissLoadingDialog()
        startActivity(
            activity?.let {
                GPDVaultActivity.create(
                    context = it.applicationContext,
                    authKey = authKey,
                    customerId = edtCustomerIdVault.text.toString(),
                    cardHolderIdentification = edtCardholderIdentification.text.toString(),
                    verifyCard = rbVerifyCardTrue.isChecked,
                    cardNameVisibility = rbCardNameVisibilityTrue.isChecked,
                    cardCvvVisibility = rbCardCvvVisibilityTrue.isChecked
                )
            }
        )
        GPDVaultActivity.gpdCallback = this
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
        Log.d("TAG", "JSON de retorno do vault: $details ")
    }

    override fun closedWithError(details: JsonObject?) {
        Toast.makeText(activity, "$details", Toast.LENGTH_LONG).show()
        Log.d("=====", "Closed with error [$details]")
    }

    override fun closedByUser() {
        Log.d("=====", "Closed by user")
    }
}