package com.apptest.ui.plan

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
import com.getnetpd.enums.client.*
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDRecurrencePlan
import com.getnetpd.models.client.GPDMigratePlansModel
import com.getnetpd.models.client.GPDPlanFloatingData
import com.getnetpd.models.client.GPDPlanPeriod
import com.getnetpd.models.client.GPDRecurrencePlanModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_plan_form.*
import java.util.*
import kotlin.collections.ArrayList

class PlanFormFragment : Fragment() {

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
            Log.d("TAG", "onCreateView: ${arg!!.key}")
        }
        return inflater.inflate(R.layout.fragment_plan_form, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSellerIdPlan.text = sellerId
        arg?.let { selectViewToShow(it.key) }
        btRequestPlan.setOnClickListener {
            if(sellerId == "" || clientId == "" || clientSecret == "") {
                Toast.makeText(context, R.string.error_missing_seller_id, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog?.startLoadingDialog()
                getToken()
            }
        }
        btClearRequestPlan.setOnClickListener {
            edtPlanName.setText("")
            edtPlanDescription.setText("")
            edtPlanAmount.setText("")
            edtPlanCurrency.setText("")
            edtPaymentTypes.setText("")
            edtSalesTax.setText("")
            edtPlanBillingCycle.setText("")
            edtSpecificCycleInDays.setText("")
            edtIdPlan.setText("")
            edtPlanId2.setText("")
            edtPlanPage.setText("")
            edtPlanLimit.setText("")
            edtPlanMigrateTo.setText("")
            edtPlanMigrateFrom.setText("")
            edtFloatingData.setText("")
            edtSubscriptionsToMigrate.setText("")
            edtPlanIdPlan.setText("")
            edtInstall.setText("")
            edtAmountFloating.setText("")
            edtInitialDate.setText("")
            edtInstallmentLimit.setText("")
            edtInstallmentStart.setText("")
            edtPaymentDate.setText("")
            edtAmount.setText("")
            edtCurrency.setText("")
            edtSalesTax2.setText("")
            edtBillingCycle.setText("")
            edtSpecificCycleInDays.setText("")
            edtSpecificCycleInDays2.setText("")
            edtStatusPlan.setText("")
            edLimit2.setText("")
            edPage2.setText("")
            edtPlanFrom.setText("")
            edtPlanTo.setText("")
        }
        btReload.setOnClickListener {
            edtPlanName.setText("Plano flex")
            edtPlanDescription.setText("Plano flex")
            edtPlanAmount.setText("19990")
            edtPlanCurrency.setText("BRL")
            edtPaymentTypes.setText("credit_card")
            edtSalesTax.setText("0")
            edtPlanBillingCycle.setText("12")
            edtSpecificCycleInDays.setText("1")
            edtIdPlan.setText("71237c12-3d13-418b-acaa-a442cdab16d0")
            edtPlanId2.setText("71237c12-3d13-418b-acaa-a442cdab16d0")
            edtPlanPage.setText("1")
            edtPlanLimit.setText("10")
            edtPlanMigrateTo.setText("33d524b5-7c96-438e-afed-ae5568db4f9b")
            edtPlanMigrateFrom.setText("71237c12-3d13-418b-acaa-a442cdab16d0")
            edtFloatingData.setText("1,12\\n2, 13")
            edtSubscriptionsToMigrate.setText("9d587d88-c77e-4fe0-afa3-aa1b6e774550\\nb03c6879-39ae-4812-8ee0-e78ca699f814")
            edtPlanIdPlan.setText("9ccdfabd-5416-40c5-b554-2567569a7725-")
            edtInstall.setText("5218893")
            edtAmountFloating.setText("77975954")
            edtInitialDate.setText("2021-12-01")
            edtInstallmentLimit.setText("3")
            edtInstallmentStart.setText("1")
            edtPaymentDate.setText("16")
            edtAmount.setText("-74453100")
            edtCurrency.setText("f")
            edtSalesTax2.setText("63681937")
            edtBillingCycle.setText("13491453")
            edtSpecificCycleInDays.setText("1")
            edtSpecificCycleInDays2.setText("-67406434")
            edtStatusPlan.setText("expired")
            edLimit2.setText("10")
            edPage2.setText("1")
            edtPlanFrom.setText("b82e0990-7d46-6e93-936f-af3100d71d66")
            edtPlanTo.setText("fb06fb4f-bd85-1c05-4af2-575fd3cb708f")
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

    private fun parseFloatingDataText(): ArrayList<GPDPlanFloatingData>? {
        if (edtFloatingData.text.toString() == "")
            return null

        val objects: ArrayList<GPDPlanFloatingData> = ArrayList()
        val content = edtFloatingData.text.toString().split("\n")
        for (line in content) {
            val lineContent = line.trim().split(',')
            objects.add(
                GPDPlanFloatingData(
                    lineContent[0].trim().toInt(),
                    lineContent[1].trim().toInt()
                )
            )
        }

        if (objects.isEmpty())
            return null
        return objects
    }

    private fun parseSubscriptionsToMigrateText(): ArrayList<String>? {
        if (edtSubscriptionsToMigrate.text.toString() == "")
            return null
        val objects: ArrayList<String> = ArrayList()
        val content = edtSubscriptionsToMigrate.text.toString().split("\n")
        for (line in content) {
            objects.add(line)
        }

        if (objects.isEmpty())
            return null
        return objects
    }

    private fun selectViewToShow(arg: Int) {
        when (arg) {
            R.string.key_new_plan -> {
                containerAddPlan.visibility = View.VISIBLE
                containerGetPlan.visibility = View.GONE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.GONE
                tvPlanId2.visibility = View.GONE
                edtPlanId2.visibility = View.GONE
            }
            R.string.key_get_plan -> {
                containerAddPlan.visibility = View.GONE
                containerGetPlan.visibility = View.VISIBLE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.GONE
                tvPlanStatus.visibility = View.GONE
                spPlanStatus.visibility = View.GONE
            }
            R.string.key_list_plans -> {
                containerAddPlan.visibility = View.GONE
                containerGetPlan.visibility = View.GONE
                containerListPlans.visibility = View.VISIBLE
                containerMigratePlans.visibility = View.GONE
            }
            R.string.key_update_plan -> {
                containerAddPlan.visibility = View.VISIBLE
                containerGetPlan.visibility = View.GONE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.GONE
            }
            R.string.key_update_plan_status -> {
                containerAddPlan.visibility = View.GONE
                containerGetPlan.visibility = View.VISIBLE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.GONE
            }
            R.string.key_migrate_plan -> {
                containerAddPlan.visibility = View.GONE
                containerGetPlan.visibility = View.GONE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.VISIBLE
            }
            R.string.key_simulate_plan -> {
                containerSimulatePlan.visibility = View.VISIBLE
                containerAddPlan.visibility = View.GONE
                containerGetPlan.visibility = View.GONE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.GONE
                containerPlan.visibility = View.GONE
                containerSimulatePlan.visibility = View.VISIBLE
            }
            R.string.key_list_plan_by_filter -> {
                containerPlan.visibility = View.VISIBLE
                tvStatusPlan.visibility = View.VISIBLE
                tvPage2.visibility = View.VISIBLE
                tvLimit2.visibility = View.VISIBLE
                containerAddPlan.visibility = View.GONE
                containerGetPlan.visibility = View.GONE
                containerListPlans.visibility = View.GONE
                containerMigratePlans.visibility = View.GONE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_new_plan -> {
                addPlan {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_plan -> {
                getPlan {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_list_plans -> {
                listPlans {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_plan -> {
                updatePlan {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_plan_status -> {
                updatePlanStatus {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_migrate_plan -> {
                migratePlans {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_simulate_plan -> {
                simulatePlan {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_list_plan_by_filter -> {
                listPlanByFilter {
                    startResponseFragment(it.toString())
                }
            }
        }
    }

    private fun emptyToNull(text: String): String? {
        if(text == "")
            return null
        return text
    }

    private fun addPlan(callback: (JsonObject) -> Unit) {
        try {
            val paymentTypes = listOf<String>(edtPaymentTypes.text.toString())

            val planPeriod = GPDPlanPeriod(
                type = GPDPlanPeriodType.valueOf(spPlanPeriodType.selectedItem.toString().toUpperCase()),
                billingCycle = if (edtPlanBillingCycle.text.toString().isEmpty()) 12 else edtPlanBillingCycle.text.toString().toInt(),
                specificCycleInDays = if (edtSpecificCycleInDays.text.toString().isEmpty()) 1 else edtSpecificCycleInDays.text.toString().toInt()
            )
            val floatingData = parseFloatingDataText()
            val plan = GPDRecurrencePlanModel(
                sellerId = sellerId,
                name = edtPlanName.text.toString(),
                amount = if (edtPlanAmount.text.toString() == "") 0 else edtPlanAmount.text.toString().toInt(),
                currency = edtPlanCurrency.text.toString(),
                description = emptyToNull(edtPlanDescription.text.toString()),
                productType = if (spPlanProductType.selectedItem.toString() != "null") GPDProductType.valueOf(spPlanProductType.selectedItem.toString().toUpperCase()) else null,
                period = planPeriod,
                salesTax = emptyToNull(edtSalesTax.text.toString())?.toInt(),
                paymentTypes = paymentTypes,
                floatingData = floatingData
            )

            GPDRecurrencePlan.create(
                authKey = authKey,
                sellerId = sellerId,
                username = "username here",
                plan = plan)
            { response ->
                callback(response)
            }
        } catch (e: Exception) {
            Log.d("TAG", "addPlan: ${e.toString()}")
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "Floating data está no formato inválido. Formato:\n<installment>, <amount>\n<installment>, <amount>", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun listPlans(callback: (JsonObject) -> Unit) {
        GPDRecurrencePlan.list(
            authKey = authKey,
            sellerId = sellerId,
            limit = edtPlanLimit.text.toString().toInt(),
            page = edtPlanPage.text.toString().toInt(),
            sortType = GPDSortType.valueOf(spSortTypePlan.selectedItem.toString())
        ) { response ->
            callback(response)
        }
    }

    private fun getPlan(callback: (JsonObject) -> Unit) {
        GPDRecurrencePlan.get(
            authKey = authKey,
            sellerId = sellerId,
            planId = edtPlanIdPlan.text.toString())
        { response ->
            callback(response)
        }
    }

    private fun updatePlan(callback: (JsonObject) -> Unit) {
        try {
            val paymentTypes = listOf<String>(edtPaymentTypes.text.toString())

            val planPeriod = GPDPlanPeriod(
                type = GPDPlanPeriodType.valueOf(spPlanPeriodType.selectedItem.toString().toUpperCase()),
                billingCycle = if (edtPlanBillingCycle.text.toString().isEmpty()) 12 else edtPlanBillingCycle.text.toString().toInt(),
                specificCycleInDays = if (edtSpecificCycleInDays.text.toString().isEmpty()) 1 else edtSpecificCycleInDays.text.toString().toInt()
            )

            val floatingData = parseFloatingDataText()
            val plan = GPDRecurrencePlanModel(
                sellerId = sellerId,
                name = edtPlanName.text.toString(),
                amount = if (edtPlanAmount.text.toString() == "") 0 else edtPlanAmount.text.toString().toInt(),
                currency = edtPlanCurrency.text.toString(),
                description = emptyToNull(edtPlanDescription.text.toString()),
                productType = if (spPlanProductType.selectedItem.toString() != "null") GPDProductType.valueOf(spPlanProductType.selectedItem.toString().toUpperCase()) else null,
                period = planPeriod,
                salesTax = emptyToNull(edtSalesTax.text.toString())?.toInt(),
                paymentTypes = paymentTypes,
                floatingData = floatingData,
                status = GPDPlanStatus.valueOf(spPlanStatus.selectedItem.toString().toUpperCase())
            )
            GPDRecurrencePlan.update(
                authKey = authKey,
                sellerId = sellerId,
                planId = edtPlanId2.text.toString(),
                plan = plan)
            { response ->
                callback(response)
            }
        }
        catch (e: Exception) {
            Log.d("TAG", "updatePlan: ${e.toString()}")
            loadingDialog?.dismissLoadingDialog()
            Toast.makeText(context, "Floating data está no formato inválido. Formato:\n<installment>, <amount>\n<installment>, <amount>", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun updatePlanStatus(callback: (JsonObject) -> Unit) {
        GPDRecurrencePlan.updateStatus(
            authKey = authKey,
            sellerId = sellerId,
            planId = edtPlanIdPlan.text.toString(),
            planStatus = GPDPlanStatus.valueOf(spPlanStatus.selectedItem.toString().toUpperCase())
        )
        { response ->
            callback(response)
        }
    }

    fun simulatePlan(callback: (JsonObject) -> Unit) {

        val planFloatingData = GPDPlanFloatingData(
            Integer.parseInt(edtInstall.text.toString()),
            Integer.parseInt(edtAmountFloating.text.toString()))


        GPDRecurrencePlan.simulate(
            authKey = authKey,
            sellerId = sellerId,
            initialDate = edtInitialDate.text.toString(),
            installmentLimit = Integer.parseInt(edtInstallmentLimit.text.toString()),
            installmentStart = Integer.parseInt(edtInstallmentStart.text.toString()),
            paymentDate = Integer.parseInt(edtPaymentDate.text.toString()),
            planId = edtIdPlan.text.toString(),
            plan = GPDRecurrencePlanModel(
                sellerId,
                "",
                "",
                Integer.parseInt(edtAmount.text.toString()),
                edtCurrency.text.toString(),
                listOf(),
                Integer.parseInt(edtSalesTax2.text.toString()),
                null,
                null,
                GPDPlanPeriod(
                    GPDPlanPeriodType.valueOf(spPlanPeriodType.selectedItem.toString().toUpperCase()),
                    Integer.parseInt(edtBillingCycle.text.toString()),
                    Integer.parseInt(edtSpecificCycleInDays.text.toString())),
                listOf(planFloatingData)

            )
        ) { response ->
            callback(response)
        }
    }

    fun listPlanByFilter(callback: (JsonObject) -> Unit) {
        GPDRecurrencePlan.listPlanByFilter(
            authKey = authKey,
            sellerId =  sellerId,
            limit =  10,
            page =  1,
            status = edtStatusPlan.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun migratePlans(callback: (JsonObject) -> Unit) {
        val subscriptions = parseSubscriptionsToMigrateText()
        val migration = GPDMigratePlansModel(
            from=edtPlanMigrateFrom.text.toString(),
            to=edtPlanMigrateTo.text.toString(),
            subscriptions=subscriptions
        )
        GPDRecurrencePlan.migrate(
            authKey = authKey,
            sellerId = sellerId,
            migrate = migration
        ) { response ->
            callback(response)
        }
    }

    private fun  startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_recurrencePlanFormFragment_to_ResponseFragment, bundle)
    }
}