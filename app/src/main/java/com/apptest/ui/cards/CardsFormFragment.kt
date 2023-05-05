package com.apptest.ui.cards

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
import com.getnetpd.enums.client.GPDCardStatus
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.managers.GPDCard
import com.getnetpd.managers.GPDConfig
import com.getnetpd.models.client.GPDCardModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_cards_form.*
import java.util.*

class CardsFormFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_cards_form, container, false)
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
            edtCardNumberToken.setText("")
            edtCardBrand.setText("")
            edtCardCardholderName.setText("")
            edtCardExpirationMonth.setText("")
            edtCardExpirationYear.setText("")
            edtCardCustomerId.setText("")
            edtCardholderIdentification2.setText("")
            edtCardSecurityCode.setText("")
            edtCardCustomerIdList.setText("")
            edtCardId.setText("")
        }
        btReload.setOnClickListener {
            edtCardNumberToken.setText("dfe05208b105578c070f806c80abd3af09e246827d29b866cf4ce16c205849977c9496cbf0d0234f42339937f327747075f68763537b90b31389e01231d4d13c")
            edtCardBrand.setText("Mastercard")
            edtCardCardholderName.setText("JOAO DA SILVA")
            edtCardExpirationMonth.setText("12")
            edtCardExpirationYear.setText("23")
            edtCardCustomerId.setText("customer_21081826")
            edtCardholderIdentification2.setText("12345678912")
            edtCardSecurityCode.setText("123")
            edtCardCustomerIdList.setText("customer_21081826")
            edtCardId.setText("14a2ce5d-ebc3-49dc-a516-cb5239b02285")
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
            R.string.key_vault_add_card -> {
                containerAddCard.visibility = View.VISIBLE
                containerListCards.visibility = View.GONE
                containerManageCard.visibility = View.GONE
            }
            R.string.key_vault_list_cards -> {
                containerAddCard.visibility = View.GONE
                containerListCards.visibility = View.VISIBLE
                containerManageCard.visibility = View.GONE
            }
            R.string.key_vault_get_card -> {
                containerAddCard.visibility = View.GONE
                containerListCards.visibility = View.GONE
                containerManageCard.visibility = View.VISIBLE
            }
            R.string.key_vault_remove_card -> {
                containerAddCard.visibility = View.GONE
                containerListCards.visibility = View.GONE
                containerManageCard.visibility = View.VISIBLE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_vault_add_card -> {
                addCard {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_vault_list_cards -> {
                listCards {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_vault_get_card -> {
                getCard {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_vault_remove_card -> {
                removeCard {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun listCards(callback: (JsonObject) -> Unit) {
        GPDCard.get(
            authKey = authKey,
            sellerId = sellerId,
            customerId = edtCardCustomerIdList.text.toString(),
            status = GPDCardStatus.valueOf(spCardStatus.selectedItem.toString())
        ) { response ->
            callback(response)
        }
    }

    private fun addCard(callback: (JsonObject) -> Unit) {

        val card = GPDCardModel(
            numberToken = edtCardNumberToken.text.toString(),
            expirationMonth = edtCardExpirationMonth.text.toString(),
            expirationYear = edtCardExpirationYear.text.toString(),
            customerId = edtCardCustomerId.text.toString(),
            cardholderName = edtCardCardholderName.text.toString(),
            brand = edtCardBrand.text.toString(),
            cardholderIdentification = edtCardholderIdentification2.text.toString(),
            verifyCard = rbVerifyCardTrue.isChecked,
            securityCode = edtCardSecurityCode.text.toString()
        )

        GPDCard.add(
            authKey = authKey,
            card = card
        ) { response ->
            callback(response)
        }
    }

    private fun getCard(callback: (JsonObject) -> Unit) {
        GPDCard.get(
            authKey = authKey,
            sellerId = sellerId,
            cardId = edtCardId.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun removeCard(callback: (JsonObject) -> Unit) {
        GPDCard.delete(
            authKey = authKey,
            sellerId = sellerId,
            cardId = edtCardId.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_cardsFormFragment_to_ResponseFragment, bundle)
    }
}