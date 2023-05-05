package com.apptest.ui.authentication3ds

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
import com.getnetpd.managers.GPDAuthentication3ds
import com.getnetpd.managers.GPDConfig
import com.getnetpd.models.TokenPayload
import com.getnetpd.models.client.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_authentication3ds_form.*
import java.util.*
import kotlin.collections.ArrayList


class Authentication3dsFormFragment : Fragment() {

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
            Log.d("TAG", "onCreateView: ${arg!!.key}")
        }
        return inflater.inflate(R.layout.fragment_authentication3ds_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdAuth3ds.text = sellerId
        arg?.let { selectViewToShow(it.key) }
        btRequest.setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()
                getToken()
            }
        }
        btClearRequestAuthentication.setOnClickListener {
            edtCardBin3ds.setText("")
            edtClientCode.setText("")
            edtCurrencyAuthentication.setText("")
            edtItems.setText("")
            edtJsVersion.setText("")
            edtOrderNumber.setText("")
            edtTotalAmount.setText("")
            edtAdditionalData.setText("")
            edtAdditionalObject.setText("")
            edtCustomerCardAlias.setText("")
            edtOverridePaymentMethod2.setText("")
            edtAlternateAuthenticationData.setText("")
            edtAlternateAuthenticationMethod.setText("")
            edtToken.setText("")
            edtNpaCode.setText("")
            edtChallengeCode.setText("")
            edtInstallmentTotalCount.setText("")
            edtMessageCategory.setText("")
            edtTransactionMode.setText("")
            edtDeviceChannel.setText("")
            edtAcsWindowSize.setText("")
            edtHttpAcceptBrowserValue.setText("")
            edtHttpAcceptContent.setText("")
            edtUserAgentBrowserValue.setText("")
            edtHttpBrowserColorDepth.setText("")
            edtHttpBrowserJavaEnabled.setText("")
            edtHttpBrowserJavaScriptEnabled.setText("")
            edtHttpBrowserLanguage.setText("")
            edtHttpBrowserScreenHeight.setText("")
            edtHttpBrowserScreenWidth.setText("")
            edtHttpBrowserTimeDifference.setText("")
            edtIpAddress2.setText("")
            edtTransactionCountYear.setText("")
            edtTransactionCountDay.setText("")
            edtAddCardAttempts.setText("")
            edtCustomerId3ds.setText("")
            edtCustomerTypeId.setText("")
            edtPaymentAccountHistory.setText("")
            edtPaymentAccountDate.setText("")
            edtPriorSuspiciousActivity.setText("")
            edtModificationHistory.setText("")
            edtAccountPurchases.setText("")
            edtShippingAddressUsageDate.setText("")
            edtFirstUseOfShippingAddress.setText("")
            edtLastChangeDate.setText("")
            edtCreatedDate.setText("")
            edtCreationHistory.setText("")
            edtPasswordChangedDate.setText("")
            edtPasswordHistory.setText("")
            edtEndDateAuthentication.setText("")
            edtFrequency.setText("")
            edtOriginalPurchaseDate.setText("")
            edtNumberToken.setText("")
            edtExpirationMonth3ds.setText("")
            edtExpirationYear3ds.setText("")
            edtTypeCard.setText("")
            edtDefaultCard.setText("")
            edtProductCode.setText("")
            edtCurrency2.setText("")
            edtTotalAmount2.setText("")
            edtAddress1BillTo.setText("")
            edtAddress2BillTo.setText("")
            edtAdministrativeAreaBillTo.setText("")
            edtCountryBillTo.setText("")
            edtEmailBillTo.setText("")
            edtFirstNameBillTo.setText("")
            edtLastNameBillTo.setText("")
            edtLocalityBillTo.setText("")
            edtMobilePhoneBillTo.setText("")
            edtPhoneNumberBillTo.setText("")
            edtPostalCodeBillTo.setText("")
            edtItems2.setText("")
            edtAddress1ShipTo.setText("")
            edtAddress2ShipTo.setText("")
            edtAdministrativeAreaShipTo.setText("")
            edtCountryShipTo.setText("")
            edtDestinationCodeShipTo.setText("")
            edtFirstNameShipTo.setText("")
            edtLastNameShipTo.setText("")
            edtLocalityShipTo.setText("")
            edtMethodShipTo.setText("")
            edtPhoneNumberShipTo.setText("")
            edtPostalCodeShipTo.setText("")
            edtCurrency3.setText("")
            edtOverridePaymentMethod3.setText("")
            edtToken2.setText("")
            edtTokenChallenge.setText("")
            edtTotalAmount3.setText("")
            edtNumberToken2.setText("")
            edtExpirationMonth3ds2.setText("")
            edtExpirationYear3ds2.setText("")
            edtTypeCard2.setText("")
            edtDefaultCard2.setText("")
            edtAdditionalData2.setText("")
            edtAdditionalObject2.setText("")
            edtCardNumber.setText("")
            edtCustomerId4.setText("")
        }

        btReload.setOnClickListener {
            edtCardBin3ds.setText("515590")
            edtClientCode.setText("string")
            edtCurrencyAuthentication.setText("BRL")
            edtItems.setText("Descricao do item,Nome do item,XXX-00-XXX-00,1,100,100")
            edtJsVersion.setText("1.0.0")
            edtOrderNumber.setText("1234-2019")
            edtTotalAmount.setText("100")
            edtAdditionalData.setText("string")
            edtAdditionalObject.setText("02")
            edtCustomerCardAlias.setText("João da Silva")
            edtOverridePaymentMethod2.setText("02")
            edtAlternateAuthenticationData.setText("string")
            edtAlternateAuthenticationMethod.setText("02")
            edtToken.setText("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzZjczZGQ3MS03NjkyLTQyYTItYjU2Ni1jYmUzMzJkYzU3OTQiLCJpYXQiOjE1NzY2NzI3NDUsImlzcyI6IjVkNzVhYzYxNmZlM2QxMmYxODhmZmQ3YyIsIk9yZ1VuaXRJZCI6IjVkNzRlZDczZTA5MTlmMTk1ODNjMzE5MyIsIlBheWxvYWQiOiJ7XCJvcmRlck51bWJlclwiOlwic3RyaW5nXCIsXCJwYXltZW50SWRcIjpcImRhMGU4ZDkwLTQ0OGMtNGJhMC1iZGUyLTQxN2Q2YTdmZjliOVwiLFwibWVyY2hhbnRJZFwiOlwiMDU0Mzk5MlwiLFwibWVyY2hhbnROYW1lXCI6XCJFQyBUZXN0ZSBIVUIg4oCTIFBBRFJBT1wiLFwiY3VycmVuY3lcIjpcImJybFwiLFwidG90YWxBbW91bnRcIjpcIjEwMDBcIixcImpzVmVyc2lvblwiOlwic3RyaW5nXCIsXCJjbGllbnRDb2RlXCI6XCJzdHJpbmctM2Y3M2RkNzEtNzY5Mi00MmEyLWI1NjYtY2JlMzMyZGM1Nzk0XCIsXCJvdmVycmlkZVBheW1lbnRNZXRob2RcIjpcIjAyXCIsXCJpdGVtc1wiOlt7XCJ0b3RhbEFtb3VudFwiOlwiOTAwXCIsXCJ1bml0UHJpY2VcIjpcIjg5OVwiLFwicXVhbnRpdHlcIjowLFwic2t1XCI6XCJzdHJpbmdcIixcImRlc2NyaXB0aW9uXCI6XCJzdHJpbmdcIixcIm5hbWVcIjpcInN0cmluZ1wifSx7XCJ0b3RhbEFtb3VudFwiOlwiOTAwXCIsXCJ1bml0UHJpY2VcIjpcIjgwMFwiLFwicXVhbnRpdHlcIjowLFwic2t1XCI6XCJzdHJpbmdcIixcImRlc2NyaXB0aW9uXCI6XCJzdHJpbmdcIixcIm5hbWVcIjpcInN0cmluZ1wifV19IiwiT2JqZWN0aWZ5UGF5bG9hZCI6ZmFsc2UsIlJlZmVyZW5jZUlkIjoiM2Y3M2RkNzEtNzY5Mi00MmEyLWI1NjYtY2JlMzMyZGM1Nzk0IiwiZXhwIjoxNTc2NjcyODY1fQ.jrPvIjtvSY6HukI-PjBE0ACWo68whA2FhCCfKeEfB4g")
            edtNpaCode.setText("01")
            edtChallengeCode.setText("01")
            edtInstallmentTotalCount.setText("03")
            edtMessageCategory.setText("01")
            edtTransactionMode.setText("M")
            edtDeviceChannel.setText("Browser")
            edtAcsWindowSize.setText("02")
            edtHttpAcceptBrowserValue.setText("text/html,application/xhtml+xml,application/xml")
            edtHttpAcceptContent.setText("text/html")
            edtUserAgentBrowserValue.setText("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0)")
            edtHttpBrowserColorDepth.setText("24")
            edtHttpBrowserJavaEnabled.setText("N")
            edtHttpBrowserJavaScriptEnabled.setText("Y")
            edtHttpBrowserLanguage.setText("pt-BR")
            edtHttpBrowserScreenHeight.setText("260")
            edtHttpBrowserScreenWidth.setText("1366")
            edtHttpBrowserTimeDifference.setText("180")
            edtIpAddress2.setText("127.0.0.1")
            edtTransactionCountYear.setText("12")
            edtTransactionCountDay.setText("1")
            edtAddCardAttempts.setText("1")
            edtCustomerId3ds.setText("246ac61a-60da-40d5-9bad-2935f210385e")
            edtCustomerTypeId.setText("CPF")
            edtPaymentAccountHistory.setText("PAYMENTACCOUNTEXISTS")
            edtPaymentAccountDate.setText("20191125")
            edtPriorSuspiciousActivity.setText("false")
            edtModificationHistory.setText("ACCOUNTUPDATEDNOW")
            edtAccountPurchases.setText("4")
            edtShippingAddressUsageDate.setText("23/11/2022")
            edtFirstUseOfShippingAddress.setText("false")
            edtLastChangeDate.setText("23/11/2022")
            edtCreatedDate.setText("23/11/2022")
            edtCreationHistory.setText("NEW_ACCOUNT")
            edtPasswordChangedDate.setText("2019-11-25")
            edtPasswordHistory.setText("PASSWORDCHANGEDNOW")
            edtEndDateAuthentication.setText("2019-11-25")
            edtFrequency.setText("4")
            edtOriginalPurchaseDate.setText("2019-11-25")
            edtNumberToken.setText("dfe05208b105578c070f806c80abd3af09e246827d29b866cf4ce16c205849977c9496cbf0d0234f42339937f327747075f68763537b90b31389e01231d4d13c")
            edtExpirationMonth3ds.setText("12")
            edtExpirationYear3ds.setText("2022")
            edtTypeCard.setText("002")
            edtDefaultCard.setText("false")
            edtProductCode.setText("01")
            edtCurrency2.setText("BRL")
            edtTotalAmount2.setText("1000")
            edtAddress1BillTo.setText("Rua XXXX, XX")
            edtAddress2BillTo.setText("Apartamento XX")
            edtAdministrativeAreaBillTo.setText("RS")
            edtCountryBillTo.setText("BR")
            edtEmailBillTo.setText("customer@email.com")
            edtFirstNameBillTo.setText("João")
            edtLastNameBillTo.setText("da Silva")
            edtLocalityBillTo.setText("Porto Alegre")
            edtMobilePhoneBillTo.setText("5551999887766")
            edtPhoneNumberBillTo.setText("555199988776")
            edtPostalCodeBillTo.setText("90230060")
            edtItems2.setText("02")
            edtAddress1ShipTo.setText("Rua XXXX, XX")
            edtAddress2ShipTo.setText("Apartamento XX")
            edtAdministrativeAreaShipTo.setText("")
            edtCountryShipTo.setText("RS")
            edtDestinationCodeShipTo.setText("01")
            edtFirstNameShipTo.setText("João")
            edtLastNameShipTo.setText("da Silva")
            edtLocalityShipTo.setText("Porto Alegre")
            edtMethodShipTo.setText("lowcost")
            edtPhoneNumberShipTo.setText("555199988776")
            edtPostalCodeShipTo.setText("686593")
            edtCurrency3.setText("BRL")
            edtOverridePaymentMethod3.setText("02")
            edtToken2.setText("string")
            edtTokenChallenge.setText("string")
            edtTotalAmount3.setText("1")
            edtNumberToken2.setText("dfe05208b105578c070f806c80abd3af09e246827d29b866cf4ce16c205849977c9496cbf0d0234f42339937f327747075f68763537b90b31389e01231d4d13c")
            edtExpirationMonth3ds2.setText("12")
            edtExpirationYear3ds2.setText("2022")
            edtTypeCard2.setText("1")
            edtDefaultCard2.setText("false")
            edtAdditionalData2.setText("string")
            edtAdditionalObject2.setText("02")
            edtCardNumber.setText("5155901222280001")
            edtCustomerId4.setText("customer_21081826")
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
            R.string.key_card_brand -> {
                containerGetCardBrand.visibility = View.VISIBLE
                containerAccessToken.visibility = View.GONE
                containerAuthentications.visibility = View.GONE
                containerGetCardToken.visibility = View.GONE
                containerAuthenticationsResult.visibility = View.GONE
            }
            R.string.key_generate_token -> {
                containerGetCardBrand.visibility = View.GONE
                containerAccessToken.visibility = View.VISIBLE
                containerAuthentications.visibility = View.GONE
                containerGetCardToken.visibility = View.GONE
                containerAuthenticationsResult.visibility = View.GONE
            }
            R.string.key_get_authentications -> {
                containerGetCardBrand.visibility = View.GONE
                containerAccessToken.visibility = View.GONE
                containerAuthentications.visibility = View.VISIBLE
                containerGetCardToken.visibility = View.GONE
                containerAuthenticationsResult.visibility = View.GONE
            }
            R.string.key_get_authentications_result -> {
                containerGetCardBrand.visibility = View.GONE
                containerAccessToken.visibility = View.GONE
                containerAuthentications.visibility = View.GONE
                containerGetCardToken.visibility = View.GONE
                containerAuthenticationsResult.visibility = View.VISIBLE
            }
            R.string.key_get_card_number_token -> {
                containerGetCardBrand.visibility = View.GONE
                containerAccessToken.visibility = View.GONE
                containerAuthentications.visibility = View.GONE
                containerGetCardToken.visibility = View.VISIBLE
                containerAuthenticationsResult.visibility = View.GONE
            }
            R.string.key_payment_authenticated -> {
                containerGetCardBrand.visibility = View.GONE
                containerAccessToken.visibility = View.GONE
                containerAuthentications.visibility = View.GONE
                containerGetCardToken.visibility = View.GONE
                containerAuthenticationsResult.visibility = View.GONE
            }
        }
    }

    private fun selectRequest(key: Int) {
        when(key) {
            R.string.key_card_brand -> {
                getCardBrand {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_generate_token -> {
                generateToken {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_authentications -> {
                getAuthentications {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_authentications_result -> {
                getAuthenticationsResult {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_card_number_token -> {
                getCardToken {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun parseItems() : ArrayList<GPDOrderItem3ds>? {
        if(edtItems.text.toString() == "")
            return null

        val objects: ArrayList<GPDOrderItem3ds> = ArrayList()
        val content = edtItems.text.toString().split("\n")
        for (line in content) {
            val lineContent = line.trim().split(',')

            val description = lineContent[0].trim()
            val name = lineContent[1].trim()
            val sku = lineContent[2].trim()
            val quantity = lineContent[3].trim().toInt()
            val totalAmount = lineContent[4].trim().toInt()
            val unitPrice = lineContent[5].trim().toInt()

            objects.add(GPDOrderItem3ds(description, name, sku, quantity, totalAmount, unitPrice))
        }
        if(objects.isEmpty())
            return null
        return objects
    }

    fun getCardBrand(callback: (JsonObject) -> Unit) {
        GPDAuthentication3ds.getCardBin(
            authKey = authKey,
            bin = edtCardBin3ds.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun generateToken(callback: (JsonObject) -> Unit) {

        var items: ArrayList<GPDOrderItem3ds>? = null
        try {
            items = parseItems()
        } catch (e: Exception) {
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "Items está no formato inválido. Verifique a documentação ou a classe utilizada.", Toast.LENGTH_SHORT).show()
            return
        }

        val order = GPDOrderModel3ds(
            clientCode = edtClientCode.text.toString(),
            currency = edtCurrencyAuthentication.text.toString(),
            items = items,
            jsVersion = edtJsVersion.text.toString(),
            orderNumber = edtOrderNumber.text.toString(),
            overridePaymentMethod = spOverridePaymentMethod.selectedItem.toString(),
            totalAmount = Helpers.textToIntIfNotEmpty(edtTotalAmount.text.toString()),
            //additionalData = edtAdditionalData.text.toString(),
            //additionalObject = edtAdditionalObject.text.toString()
        )

        GPDAuthentication3ds.generateToken(
            authKey = authKey,
            sellerId = sellerId,
            order = order
        ) { response ->
            callback(response)
        }
    }

    private fun getAuthentications(callback: (JsonObject) -> Unit) {
        val authentication = GPDAuthentication(
            token = edtToken.text.toString(),
            npaCode = edtNpaCode.text.toString(),
            challengeCode = edtChallengeCode.text.toString(),
            installmentTotalCount = Helpers.textToIntIfNotEmpty(edtInstallmentTotalCount.text.toString()),
            messageCategory = edtMessageCategory.text.toString(),
            transactionMode = edtTransactionMode.text.toString(),
            deviceChannel = edtDeviceChannel.text.toString(),
            acsWindowSize = edtAcsWindowSize.text.toString()
        )

        val device = GPDAuthentication3dsDevice(
            httpAcceptBrowserValue = edtHttpAcceptBrowserValue.text.toString(),
            httpAcceptContent = edtHttpAcceptContent.text.toString(),
            userAgentBrowserValue = edtUserAgentBrowserValue.text.toString(),
            httpBrowserColorDepth = edtHttpBrowserColorDepth.text.toString(),
            httpBrowserJavaEnabled = edtHttpBrowserJavaEnabled.text.toString(),
            httpBrowserLanguage = edtHttpBrowserLanguage.text.toString(),
            httpBrowserScreenHeight = edtHttpBrowserScreenHeight.text.toString(),
            httpBrowserScreenWidth = edtHttpBrowserScreenWidth.text.toString(),
            httpBrowserTimeDifference = edtHttpBrowserTimeDifference.text.toString(),
            ipAddress = edtIpAddress2.text.toString()
        )

        val account = GPDAccount(
            modificationHistory = edtModificationHistory.text.toString(),
            accountPurchases = Helpers.textToIntIfNotEmpty(edtAccountPurchases.text.toString()),
            shippingAddressUsageDate = edtShippingAddressUsageDate.text.toString(),
            firstUseOfShippingAddress = edtFirstUseOfShippingAddress.text.toString().toBoolean(),
            lastChangeDate = edtLastChangeDate.text.toString(),
            createdDate = edtCreatedDate.text.toString(),
            creationHistory = edtCreationHistory.text.toString(),
            passwordChangedDate = edtPasswordChangedDate.text.toString(),
            passwordHistory = edtPasswordHistory.text.toString()
        )

        val customerRiskInfos = GPDCustomerRiskInfos(
            transactionCountYear = Helpers.textToIntIfNotEmpty(edtTransactionCountYear.text.toString()),
            transactionCountDay = Helpers.textToIntIfNotEmpty(edtTransactionCountDay.text.toString()),
            addCardAttempts = Helpers.textToIntIfNotEmpty(edtAddCardAttempts.text.toString()),
            customerId = edtCustomerId3ds.text.toString(),
            customerTypeId = edtCustomerTypeId.text.toString(),
            paymentAccountHistory = edtPaymentAccountHistory.text.toString(),
            paymentAccountDate = edtPaymentAccountDate.text.toString(),
            priorSuspiciousActivity = edtPriorSuspiciousActivity.text.toString(),
            account = account
        )

        val recurring = GPDRecurring(
            endDate = edtEndDateAuthentication.text.toString(),
            frequency = Helpers.textToIntIfNotEmpty(edtFrequency.text.toString()),
            originalPurchaseDate = edtOriginalPurchaseDate.text.toString()
        )

        val card = GPDAuthenticationCard(
            numberToken = edtNumberToken.text.toString(),
            expirationMonth = edtExpirationMonth3ds.text.toString(),
            expirationYear = edtExpirationYear3ds.text.toString(),
            typeCard = edtTypeCard.text.toString(),
            defaultCard = edtDefaultCard.text.toString().toBoolean()
        )

        val billTo = GPDBillTo(
            address1 = edtAddress1BillTo.text.toString(),
            address2 = edtAddress2BillTo.text.toString(),
            administrativeArea = edtAdministrativeAreaBillTo.text.toString(),
            country = edtCountryBillTo.text.toString(),
            email = edtEmailBillTo.text.toString(),
            firstName = edtFirstNameBillTo.text.toString(),
            lastName = edtLastNameBillTo.text.toString(),
            locality = edtLocalityBillTo.text.toString(),
            mobilePhone = edtMobilePhoneBillTo.text.toString(),
            phoneNumber = edtPhoneNumberBillTo.text.toString(),
            postalCode = edtPostalCodeBillTo.text.toString()
        )

        val shipTo = GPDShipTo(
            address1 = edtAddress1ShipTo.text.toString(),
            address2 = edtAddress2ShipTo.text.toString(),
            administrativeArea = edtAdministrativeAreaShipTo.text.toString(),
            country = edtCountryShipTo.text.toString(),
            destinationCode = edtDestinationCodeShipTo.text.toString(),
            firstName = edtFirstNameShipTo.text.toString(),
            lastName = edtLastNameShipTo.text.toString(),
            locality = edtLocalityShipTo.text.toString(),
            method = edtMethodShipTo.text.toString(),
            phoneNumber = edtPhoneNumberShipTo.text.toString(),
            postalCode = edtPostalCodeShipTo.text.toString()
        )

        val order = GPDAuthenticationOrder(
            productCode = edtProductCode.text.toString(),
            currency = edtCurrency2.text.toString(),
            totalAmount = Helpers.textToIntIfNotEmpty(edtTotalAmount2.text.toString()),
            billTo = billTo,
            shipTo = shipTo,
            items = parseItems()
        )

        val authentications = GPDAuthentications3ds(
            customerCardAlias = edtCustomerCardAlias.text.toString(),
            overridePaymentMethod = edtOverridePaymentMethod2.text.toString(),
            alternateAuthenticationData = edtAlternateAuthenticationData.text.toString(),
            alternateAuthenticationMethod = edtAlternateAuthenticationMethod.text.toString(),
            authentication = authentication,
            device = device,
            customerRiskInformation = customerRiskInfos,
            recurring = recurring,
            card = card,
            order = order
        )


        GPDAuthentication3ds.getAuthentications(
            authKey = authKey,
            authentications = authentications
        ) { response ->
            callback(response)
        }
    }

    private fun getAuthenticationsResult(callback: (JsonObject) -> Unit) {

        val card = GPDAuthenticationCard(
            numberToken = edtNumberToken2.text.toString(),
            expirationMonth = edtExpirationMonth3ds2.text.toString(),
            expirationYear = edtExpirationYear3ds2.text.toString(),
            typeCard = edtTypeCard2.text.toString(),
            defaultCard = edtDefaultCard2.text.toString().toBoolean()
        )

        val authenticationsResult = GPDAuthenticationResult(
            currency = edtCurrency3.text.toString(),
            overridePaymentMethod = edtOverridePaymentMethod3.text.toString(),
            token = edtToken2.text.toString(),
            tokenChallenge = edtTokenChallenge.text.toString(),
            totalAmount = Helpers.textToIntIfNotEmpty(edtTotalAmount3.text.toString()),
            card = card,
            additionalData = JsonObject(),
            additionalObject = JsonObject()
        )

        GPDAuthentication3ds.getAuthenticationsResult(
            authKey = authKey,
            authenticationsResult = authenticationsResult
        ) { response ->
            callback(response)
        }
    }

    private fun getCardToken(callback: (JsonObject) -> Unit) {
        val cardPayload = TokenPayload(
            cardNumber = edtCardNumber.text.toString(),
            customerID = edtCustomerId4.text.toString()
        )

        GPDAuthentication3ds.getCardNumberTokenized(
            authKey = authKey,
            card = cardPayload
        ) { response ->
            callback(response)
        }
    }

    private fun  startResponseFragment(response : String) {
        val bundle = Bundle()
        bundle.putString("response", response)
        loadingDialog?.dismissLoadingDialog()
        findNavController().navigate(R.id.action_authentication3dsFormFragment_to_ResponseFragment, bundle)
    }
}