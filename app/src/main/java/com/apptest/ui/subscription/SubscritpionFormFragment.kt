package com.apptest.ui.subscription

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apptest.R
import com.apptest.model.Methods
import com.apptest.services.ServiceRepository
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.enums.client.GPDSortType
import com.getnetpd.enums.client.GPDTransactionType
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDSubscription
import com.getnetpd.models.client.GPDAddressModel
import com.getnetpd.models.client.GPDCardModel
import com.getnetpd.models.client.GPDDeviceModel
import com.getnetpd.models.client.GPDShippingModel
import com.getnetpd.models.subscription.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_subscription_form.*
import java.util.*

class SubscritpionFormFragment : Fragment() {

    private var gpdCardModel = GPDCardModel()
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
        return inflater.inflate(R.layout.fragment_subscription_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdSubs.text = sellerId
        arg?.let { selectViewToShow(it.key) }
        view.findViewById<Button>(R.id.btRequest).setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()
                getToken()
            }
        }
        btClearRequest.setOnClickListener {
            edtCustomerId.setText("")
            edtFirstName.setText("")
            edtName.setText("")
            edtPlanId.setText("")
            edtOrderId.setText("")
            edtPhoneNumber.setText("")
            edtShippingsAmount.setText("")
            edtEmail.setText("")
            edtStreet.setText("")
            edtNumber.setText("")
            edtComplement.setText("")
            edtDistrict.setText("")
            edtCity.setText("")
            edtState.setText("")
            edtCountry.setText("")
            edtPostalCode.setText("")
            edtNumberInstall.setText("")
            edtSoftyDescriptor.setText("")
            edtIpAddress.setText("")
            edtDeviceId.setText("")
            edtStartDate.setText("")
            edtSubscriptionId.setText("")
            edtPage.setText("")
            edtLimit.setText("")
            edtStatusDetail.setText("")
            edtChargeId.setText("")
            edtInstallment.setText("")
            edtNewAmount.setText("")
            edtNewDate.setText("")
            edtFromPlan.setText("")
            edtToPlan.setText("")
            edtCardHolderName.setText("")
            edtSecurityCode.setText("")
            edtBrand.setText("")
            edtExpirationMonth.setText("")
            edtExpirationYear.setText("")
            edtBin.setText("")
            edtDay.setText("")
            edtPlanId.setText("")
            edtSort.setText("")
            edtStatus.setText("")
            edtSubscriptionId.setText("")
            edtEndDate.setText("")
            edtFirstTransition.setText("")
            edtLastTransiction.setText("")
            edtNextScheduleDate.setText("")
            edtStatus.setText("")
            edtStatusDetail2.setText("")

        }

        btReload.setOnClickListener {
            edtCustomerId.setText("026ce374-2451-4ad2-a8e4-b5d8b42ed1d5")
            edtFirstName.setText("João")
            edtName.setText("da Silva")
            edtPlanId.setText("33d524b5-7c96-438e-afed-ae5568db4f9b")
            edtOrderId.setText("6d2e4380-d8a3-4ccb-9138-c289182818a3")
            edtPhoneNumber.setText("5551999887766")
            edtShippingsAmount.setText("3000")
            edtEmail.setText("customer@email.com.br")
            edtStreet.setText("Av. Brasil")
            edtNumber.setText("1000")
            edtComplement.setText("Sala 1")
            edtDistrict.setText("São Geraldo")
            edtCity.setText("Porto Alegre")
            edtState.setText("RS")
            edtCountry.setText("Brasil")
            edtPostalCode.setText("90230060")
            edtNumberInstall.setText("1")
            edtSoftyDescriptor.setText("LOJA*TESTE*COMPRA-123")
            edtIpAddress.setText("127.0.0.1")
            edtDeviceId.setText("hash-device-id")
            edtStartDate.setText("2021-11-03")
            edtSubscriptionId.setText("96708050-d943-4010-819c-6f22da06a155")
            edtPage.setText("1")
            edtLimit.setText("10")
            edtStatusDetail.setText("Cliente não tem mais interesse no serviço/produto")
            edtChargeId.setText("52bc81b2-f079-4f7b-b63a-f653ff29d794")
            edtInstallment.setText("2")
            edtNewAmount.setText("19990")
            edtNewDate.setText("2020-06-10")
            edtFromPlan.setText("47ed1217-340c-41c0-b873-1fadc8593751")
            edtToPlan.setText("51995e24-b1ae-4826-8e15-2a568a87abdd")
            edtCardHolderName.setText("JOAO SILVA")
            edtSecurityCode.setText("321")
            edtBrand.setText("Mastercard")
            edtExpirationMonth.setText("09")
            edtExpirationYear.setText("22")
            edtBin.setText("123412")
            edtDay.setText("17")
            edtPlanId.setText("33d524b5-7c96-438e-afed-ae5568db4f9b")
            edtSort.setText("status")
            edtStatus.setText("expired")
            edtSubscriptionId.setText("96708050-d943-4010-819c-6f22da06a155")
            edtEndDate.setText("2021-12-12")
            edtFirstTransition.setText("j89hf98hs09sd9")
            edtLastTransiction.setText("hbs9yojsyoh8002esw")
            edtNextScheduleDate.setText("2021-12-02")
            edtStatus.setText("expired")
            edtStatusDetail2.setText("Teste")

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

    fun selectViewToShow(key : Int) {
        when(key) {
            R.string.key_new_subscription -> {
                containerAddSubscription.visibility = View.VISIBLE
            }
            R.string.key_list_subscriptions -> {
                containerListSubscriptions.visibility = View.VISIBLE
            }

            R.string.key_get_subscription -> {
                containerSubscriptions.visibility = View.VISIBLE
            }

            R.string.key_alter_subscription -> {
                containerListSubscriptions.visibility = View.VISIBLE
                tPage.visibility = View.GONE
                edtPage.visibility = View.GONE
                tLimit.visibility = View.GONE
                edtLimit.visibility = View.GONE
                tvSortType.visibility = View.GONE
                spSortType.visibility = View.GONE
                tFirstTransition.visibility = View.VISIBLE
                edtFirstTransition.visibility = View.VISIBLE
                tvSubscriptionId.visibility = View.VISIBLE
                edtSubscriptionId.visibility = View.VISIBLE
                tEndDate.visibility = View.VISIBLE
                tLastTransiction.visibility = View.VISIBLE
                tNextScheduleDate.visibility = View.VISIBLE
                TStatus.visibility = View.VISIBLE
                tStatusDetail2.visibility = View.VISIBLE
            }

            R.string.key_cancel_subscription -> {
                containerSubscriptions.visibility = View.VISIBLE
                TStatusDetail.visibility = View.VISIBLE
            }
            R.string.key_billing_projection -> {
                containerSubscriptions.visibility = View.VISIBLE
                TStatusDetail.visibility = View.GONE
                TChargeId.visibility = View.GONE
                TDay.visibility = View.GONE
            }
            R.string.key_confirm_payment -> {
                containerSubscriptions.visibility = View.VISIBLE
                TStatusDetail.visibility = View.VISIBLE
                edtStatusDetail.setText("Pagamento de assinatura confirmado")
                TChargeId.visibility = View.VISIBLE
            }
            R.string.key_update_date_charge -> {
                containerSubscriptions.visibility = View.VISIBLE
                TDay.visibility = View.VISIBLE
            }
            R.string.key_update_data_payment -> {
                containerSubscriptions.visibility = View.VISIBLE
                TCardHolderName.visibility = View.VISIBLE
                TSecurityCode.visibility = View.VISIBLE
                TBrand.visibility = View.VISIBLE
                TExpirationMonth.visibility = View.VISIBLE
                TExpirationYear.visibility = View.VISIBLE
                TBin.visibility = View.VISIBLE
            }
            R.string.key_register_change_date_value -> {
                containerSubscriptions.visibility = View.VISIBLE
                TInstallment.visibility = View.VISIBLE
                TNewAmount.visibility = View.VISIBLE
                TNewDate.visibility = View.VISIBLE
            }
            R.string.key_list_changes -> {
                containerSubscriptions.visibility = View.VISIBLE
            }
            R.string.key_remove_changes -> {
                containerSubscriptions.visibility = View.VISIBLE
            }

            R.string.key_add_new_change -> {
                containerSubscriptions.visibility = View.VISIBLE
                TInstallment.visibility = View.VISIBLE
                TNewAmount.visibility = View.VISIBLE
                TNewDate.visibility = View.VISIBLE
            }
            R.string.key_delete_change -> {
                containerSubscriptions.visibility = View.VISIBLE
                TInstallment.visibility = View.VISIBLE
            }
            R.string.key_update_date_or_value -> {
                containerSubscriptions.visibility = View.VISIBLE
                TNewAmount.visibility = View.VISIBLE
                TNewDate.visibility = View.VISIBLE
            }
            R.string.key_alter_plan_subscription -> {
                containerSubscriptions.visibility = View.VISIBLE
                TFromPlan.visibility = View.VISIBLE
                TToPlan.visibility = View.VISIBLE
            }
            R.string.key_list_charges -> {
                containerListSubscriptions.visibility = View.VISIBLE
            }

            R.string.key_cancel_subscription_by_seller_id -> {
                containerSubscriptions.visibility = View.VISIBLE
                tvSubscriptionId.visibility = View.GONE
                edtSubscriptionId.visibility = View.GONE
            }

            R.string.key_get_subscription_by_plan -> {
                containerListSubscriptions.visibility = View.VISIBLE
                tPage.visibility = View.GONE
                edtPage.visibility = View.GONE
                tLimit.visibility = View.GONE
                edtLimit.visibility = View.GONE
                TStatus.visibility = View.VISIBLE
                TPlanId.visibility = View.VISIBLE
                tSort.visibility = View.VISIBLE

            }
        }
    }

    private fun selectRequest(key: Int) {
        when(key) {
            R.string.key_new_subscription -> {
                newSubscription {
                  startResponseFragment(it.toString())
                }
            }
            R.string.key_list_subscriptions ->{
                listSubscription {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_subscription ->{
                getRecurrenceSubscription {
                    startResponseFragment(it.toString())
                }
            }

            R.string.key_alter_subscription ->{
                alterSubscription {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_cancel_subscription ->{
                cancelSubscription {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_billing_projection ->{
                getBillingProjection {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_confirm_payment ->{
                confirmPaymentSubscription {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_date_charge ->{
                updateDatePayment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_data_payment ->{
                updateDataPayment {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_register_change_date_value ->{
                registerDateOrValueChange {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_list_changes ->{
                getListChangeDataOrValue {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_remove_changes ->{
                removeChangesDataOrValue {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_add_new_change ->{
                addNewChangesDateOrValue {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_delete_change ->{
                removeChangeDataOrValue {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_date_or_value ->{
                updateDateOrValue {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_alter_plan_subscription ->{
                alterPlanSubscription {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_list_charges  ->{
                listChanges {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_cancel_subscription_by_seller_id  ->{
                cancelSubscriptionBySellerID {
                    startResponseFragment(it.toString())
                }
            }

            R.string.key_get_subscription_by_plan  ->{
                getSubscriptionByPlan {
                    startResponseFragment(it[0].toString())
                }
            }

        }
    }

    fun newSubscription(callback: (JsonObject) -> Unit) {
        val address = GPDAddressModel(
            street = edtStreet.text.toString(),
            number = edtNumber.text.toString(),
            complement = edtComplement.text.toString(),
            district = edtDistrict.text.toString(),
            city = edtCity.text.toString(),
            state = edtState.text.toString(),
            country = edtCountry.text.toString(),
            postalCode = edtPostalCode.text.toString()
        )


        val credit = GPDCredit(
            transaction_type = GPDTransactionType.valueOf(spPaymentType.selectedItem.toString()),
            number_installments = Integer.parseInt(edtNumberInstall.text.toString()),
            soft_descriptor = edtSoftyDescriptor.text.toString(),
            billing_address = address,
            card =gpdCardModel
        )
        val paymentType = GPDPaymentType(
            credit = credit
        )
        val shipping = GPDShippingModel(
            firstName = edtFirstName.text.toString(),
            name = edtName.text.toString(),
            email = edtEmail.text.toString(),
            phoneNumber =edtPhoneNumber.text.toString(),
            shippingAmount = Integer.parseInt(edtShippingsAmount.text.toString()),
            address = address
        )
        val subscriptions = GPDSubscriptionModel(
            payment_type = paymentType,
            shippings = listOf(shipping)
        )
        val device = GPDDeviceModel(
            ipAddress = edtIpAddress.text.toString(),
            deviceId = edtDeviceId.text.toString()
        )
        val newSubscription = GPDNewSubscriptionsModel(
            sellerId = sellerId,
            customerId = edtCustomerId.text.toString(),
            plan_id = edtPlanId.text.toString(),
            order_id = edtOrderId.text.toString(),
            subscription = subscriptions,
            device = device,
            start_date = edtStartDate.text.toString()

        )

        GPDSubscription.newSubscription(
            authKey,
            sellerId,
            newSubscription
        ) { response ->
            callback(response)
        }
    }

    fun listSubscription(callback: (JsonObject) -> Unit) {
        GPDSubscription.list(
            authKey = authKey,
            sellerId = sellerId,
            limit = edtLimit.text.toString().toInt(),
            page = edtPage.text.toString().toInt(),
            sortType = GPDSortType.valueOf(spSortType.selectedItem.toString())
        ) { response ->
            callback(response)
        }
    }

    fun getRecurrenceSubscription(callback: (JsonObject) -> Unit) {
        GPDSubscription.getRecurrenceSubscription(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString()
        ) { response ->
            callback(response)
        }
    }

    fun alterSubscription(callback: (JsonObject) -> Unit) {

        GPDSubscription.alterSubscription(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            alterSubscription = GPDAlterSubscription(
                edtEndDate.text.toString()+"T12:35:16.254Z",
                edtFirstTransition.text.toString(),
                edtLastTransiction.text.toString(),
                edtNextScheduleDate.text.toString()+"T12:35:16.254Z",
                edtStatus.text.toString(),
                edtStatusDetail2.text.toString()
            )
        ) { response ->
            callback(response)
        }
    }

    fun cancelSubscription(callback: (JsonObject) -> Unit) {
        val cancelSubscriptionModel = GPDCancelSubscription(
            seller_id = sellerId,
            status_details= edtStatusDetail.text.toString()
        )
        GPDSubscription.cancelSubscription(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            cancelSubscriptionModel = cancelSubscriptionModel
        ) { response ->
            callback(response)        }
    }

    fun getBillingProjection(callback: (JsonObject) -> Unit) {
        GPDSubscription.getBillingProjection(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString()
        ) { response ->
            callback(response)
        }
    }

    fun confirmPaymentSubscription(callback: (JsonObject) -> Unit) {
        val confirmPaymentSubscription = GPDConfirmPaymentSubscription(
            seller_id = sellerId,
            status_details = edtStatusDetail.text.toString(),
            charge_id = edtChargeId.text.toString()
        )
        GPDSubscription.confirmPaymentSubscription(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            confirmPaymentSubscription = confirmPaymentSubscription
        ) { response ->
            callback(response)        }
    }

    fun updateDatePayment(callback: (JsonObject) -> Unit) {

        GPDSubscription.updateDatePayment(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            day = edtDay.text.toString().toInt()
        ) { response ->
            callback(response)        }
    }


    fun updateDataPayment(callback: (JsonObject) -> Unit) {
        val card = GPDCardModel(
            numberToken = tokenNumber,
            cardholderName = edtCardHolderName.text.toString(),
            securityCode =  edtSecurityCode.text.toString(),
            brand =  edtBrand.text.toString(),
            expirationMonth =  edtExpirationMonth.text.toString(),
            expirationYear =  edtExpirationYear.text.toString(),
            bin =  edtBin.text.toString()
        )

        GPDSubscription.updateDataPayment(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            card = card
        ) { response ->
            callback(response)
        }
    }

    fun registerDateOrValueChange(callback: (JsonObject) -> Unit) {

        val alterDateOrValue = GPDRegisterDateOrValue(
            edtInstallment.text.toString().toInt(),
            edtNewAmount.text.toString().toInt(),
            edtNewDate.text.toString()

        )


        GPDSubscription.registerDateOrValueChange(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            registerDateOrValue = arrayOf(alterDateOrValue)
        ) { response ->
            callback(response)        }
    }

    fun getListChangeDataOrValue(callback: (JsonObject) -> Unit) {
        GPDSubscription.getListChangeDataOrValue(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString()
        ) { response ->
            callback(response)        }
    }


    fun removeChangesDataOrValue(callback: (JsonObject) -> Unit) {
        GPDSubscription.deleteChangesDataOrValue(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString()
        ) { response ->
            callback(response)
        }
    }

    fun addNewChangesDateOrValue(callback: (JsonObject) -> Unit) {

        val alterDateOrValue = GPDRegisterDateOrValue(
            edtInstallment.text.toString().toInt(),
            edtNewAmount.text.toString().toInt(),
            edtNewDate.text.toString()

        )

        GPDSubscription.addNewChangesDateOrValue(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            registerDateOrValue = arrayOf(alterDateOrValue)
        ) { response ->
            callback(response)
        }
    }
    fun removeChangeDataOrValue(callback: (JsonObject) -> Unit) {
        GPDSubscription.deleteChangeDataOrValue(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            installment = edtInstallment.text.toString().toInt()
        ) { response ->
            callback(response)
        }
    }

    fun updateDateOrValue(callback: (JsonObject) -> Unit) {

        val alterDateOrValue = GPDAlterDateOrValue(
            edtNewAmount.text.toString().toInt(),
            edtNewDate.text.toString()
        )

        GPDSubscription.updateDateOrValue(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            installment = edtInstallment.text.toString().toInt(),
            alterDateOrValue = alterDateOrValue
        ) { response ->
            callback(response)
        }
    }

    fun alterPlanSubscription(callback: (JsonObject) -> Unit) {

        val alterPlanSubscription = GPDAlterPlanSubscription(
            edtFromPlan.text.toString(),
            edtToPlan.text.toString()
        )

        GPDSubscription.alterPlanSubscription(
            authKey = authKey,
            sellerId = sellerId,
            subscriptionId = edtSubscriptionId.text.toString(),
            alterPlanSubscription = alterPlanSubscription
        ) { response ->
            callback(response)        }
    }

    fun listChanges(callback: (JsonObject) -> Unit) {
        GPDSubscription.listCharges(
            authKey = authKey,
            sellerId = sellerId,
            limit = edtLimit.text.toString().toInt(),
            page =  edtPage.text.toString().toInt(),
            sortType = GPDSortType.valueOf(spSortType.selectedItem.toString())
        ) { response ->
            callback(response)
        }
    }

    fun cancelSubscriptionBySellerID(callback: (JsonObject) -> Unit) {


        GPDSubscription.cancelSubscriptionBySellerID(
            authKey = authKey,
            sellerId = sellerId,
            cancelSubscription =  GPDCancelSubscriptionBySellerId(
                sellerId,
                "Teste de cancelamento de assinatura 2"
            )
        ) { response ->
            callback(response)
        }
    }

    fun getSubscriptionByPlan(callback: (Array<JsonObject>) -> Unit) {
        GPDSubscription.getSubscriptionByPlan(
            authKey = authKey,
            sellerId = sellerId,
            planId= edtPlanId.text.toString(),
            sortType = GPDSortType.valueOf(spSortType.selectedItem.toString()),
            sort = edtSort.text.toString(),
            status = edtStatus.text.toString()
        ) { response ->
            callback(response)
        }
    }


    private fun  startResponseFragment(response : String) {
        val bundle = Bundle()
        bundle.putString("response", response)
        loadingDialog?.dismissLoadingDialog()
        findNavController().navigate(R.id.action_subscriptionFormFragment_to_ResponseFragment, bundle)
    }
}