package com.apptest.ui.customer

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
import com.apptest.utils.Helpers
import com.apptest.utils.LoadingDialog
import com.getnetpd.enums.client.GPDCustomerStatus
import com.getnetpd.enums.client.GPDDocumentType
import com.getnetpd.enums.client.GPDEnviroment
import com.getnetpd.enums.client.GPDSortType
import com.getnetpd.managers.GPDConfig
import com.getnetpd.managers.GPDRecurrenceCustomer
import com.getnetpd.models.client.GPDAddressModel
import com.getnetpd.models.client.GPDRecurrenceCustomerModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_customer_form.*
import java.util.*

class CustomerFormFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_customer_form, container, false)
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
            edtCustomerId.setText("")
            edtFirstName.setText("")
            edtLastName.setText("")
            edtDocumentNumber.setText("")
            edtBirthDate.setText("")
            edtPhoneNumber.setText("")
            edtCellphoneNumber.setText("")
            edtEmail.setText("")
            edtObservation.setText("")
            edtStreet.setText("")
            edtNumber.setText("")
            edtComplement.setText("")
            edtDistrict.setText("")
            edtCity.setText("")
            edtState.setText("")
            edtCountry.setText("")
            edtPostalCode.setText("")
            edtCustomerId2.setText("")
            edtPage.setText("")
            edtLimit.setText("")
        }
        btReload.setOnClickListener {
            edtCustomerId.setText("customer_21081826")
            edtFirstName.setText("João")
            edtLastName.setText("da Silva")
            edtDocumentNumber.setText("66461042040")
            edtBirthDate.setText("1976-02-21")
            edtPhoneNumber.setText("5551999887766")
            edtCellphoneNumber.setText("5551999887766")
            edtEmail.setText("customer@email.com.br")
            edtObservation.setText("O cliente tem interesse no plano x.")
            edtStreet.setText("Av. Brasil")
            edtNumber.setText("1000")
            edtComplement.setText("Sala 1")
            edtDistrict.setText("São Geraldo")
            edtCity.setText("Porto Alegre")
            edtState.setText("RS")
            edtCountry.setText("Brasil")
            edtPostalCode.setText("90230060")
            edtCustomerId2.setText("customer_21081826")
            edtPage.setText("1")
            edtLimit.setText("10")
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
            R.string.key_new_customer -> {
                containerAddCustomer.visibility = View.VISIBLE
                containerGetCustomer.visibility = View.GONE
                containerListCustomers.visibility = View.GONE
            }
            R.string.key_get_customer -> {
                containerAddCustomer.visibility = View.GONE
                containerGetCustomer.visibility = View.VISIBLE
                containerListCustomers.visibility = View.GONE
                tvStatus.visibility = View.GONE
                spStatus.visibility = View.GONE
            }
            R.string.key_list_customers -> {
                containerAddCustomer.visibility = View.GONE
                containerGetCustomer.visibility = View.GONE
                containerListCustomers.visibility = View.VISIBLE
            }
            R.string.key_update_customer -> {
                containerAddCustomer.visibility = View.VISIBLE
                containerGetCustomer.visibility = View.GONE
                containerListCustomers.visibility = View.GONE
            }
            R.string.key_update_customer_status -> {
                containerAddCustomer.visibility = View.GONE
                containerGetCustomer.visibility = View.VISIBLE
                containerListCustomers.visibility = View.GONE
            }
        }
    }

    private fun selectRequest(arg: Int) {
        when(arg) {
            R.string.key_new_customer -> {
                addCustomer {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_get_customer -> {
                getCustomer {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_list_customers -> {
                listCustomers {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_customer -> {
                updateCustomer {
                    startResponseFragment(it.toString())
                }
            }
            R.string.key_update_customer_status -> {
                updateCustomerStatus {
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

    private fun checkIfAddressFieldsAllNull(address: GPDAddressModel): GPDAddressModel? {
        if(address.city.isNullOrEmpty() &&
            address.complement.isNullOrEmpty() &&
            address.country.isNullOrEmpty() &&
            address.district.isNullOrEmpty() &&
            address.number.isNullOrEmpty() &&
            address.postalCode.isNullOrEmpty() &&
            address.state.isNullOrEmpty() &&
            address.street.isNullOrEmpty())
                return null
        return address
    }

    private fun addCustomer(callback: (JsonObject) -> Unit) {
        val address = GPDAddressModel(
            street = emptyToNull(edtStreet.text.toString()),
            number = emptyToNull(edtNumber.text.toString()),
            complement = emptyToNull(edtComplement.text.toString()),
            district = emptyToNull(edtDistrict.text.toString()),
            city = emptyToNull(edtCity.text.toString()),
            state = emptyToNull(edtState.text.toString()),
            country = emptyToNull(edtCountry.text.toString()),
            postalCode = emptyToNull(edtPostalCode.text.toString())
        )
        val customer = GPDRecurrenceCustomerModel(
            sellerId = sellerId,
            firstName = edtFirstName.text.toString(),
            lastName = edtLastName.text.toString(),
            documentType = GPDDocumentType.valueOf(spDocumentType.selectedItem.toString().toUpperCase()),
            documentNumber = edtDocumentNumber.text.toString(),
            birthDate = emptyToNull(edtBirthDate.text.toString()),
            phoneNumber = emptyToNull(edtPhoneNumber.text.toString()),
            celphoneNumber = emptyToNull(edtCellphoneNumber.text.toString()),
            email = emptyToNull(edtEmail.text.toString()),
            observation = emptyToNull(edtObservation.text.toString()),
            address = checkIfAddressFieldsAllNull(address)
        )
        GPDRecurrenceCustomer.create(
            authKey = authKey,
            sellerId = sellerId,
            customer = customer
        ) { response ->
            callback(response)
        }
    }

    private fun getCustomer(callback: (JsonObject) -> Unit) {
        GPDRecurrenceCustomer.get(
            authKey = authKey,
            sellerId = sellerId,
            customerId = edtCustomerId2.text.toString()
        ) { response ->
            callback(response)
        }
    }

    private fun listCustomers(callback: (JsonObject) -> Unit) {
        GPDRecurrenceCustomer.list(
            authKey = authKey,
            sellerId = sellerId,
            limit = Helpers.textToIntIfNotEmpty(edtLimit.text.toString()),
            page = Helpers.textToIntIfNotEmpty(edtPage.text.toString()),
            sortType = GPDSortType.valueOf(spCustomerSortType.selectedItem.toString())
        ) { response ->
            callback(response)
        }
    }

    private fun updateCustomer(callback: (JsonObject) -> Unit) {
        val address = GPDAddressModel(
            street = emptyToNull(edtStreet.text.toString()),
            number = emptyToNull(edtNumber.text.toString()),
            complement = emptyToNull(edtComplement.text.toString()),
            district = emptyToNull(edtDistrict.text.toString()),
            city = emptyToNull(edtCity.text.toString()),
            state = emptyToNull(edtState.text.toString()),
            country = emptyToNull(edtCountry.text.toString()),
            postalCode = emptyToNull(edtPostalCode.text.toString())
        )
        val customer = GPDRecurrenceCustomerModel(
            sellerId = sellerId,
            firstName = edtFirstName.text.toString(),
            lastName = edtLastName.text.toString(),
            documentType = GPDDocumentType.CPF,
            documentNumber = edtDocumentNumber.text.toString(),
            birthDate = emptyToNull(edtBirthDate.text.toString()),
            phoneNumber = emptyToNull(edtPhoneNumber.text.toString()),
            celphoneNumber = emptyToNull(edtCellphoneNumber.text.toString()),
            email = emptyToNull(edtEmail.text.toString()),
            observation = emptyToNull(edtObservation.text.toString()),
            address = address
        )
        GPDRecurrenceCustomer.update(
            authKey = authKey,
            sellerId = sellerId,
            customerId = edtCustomerId.text.toString(),
            customer = customer
        ) { response ->
            callback(response)
        }
    }

    private fun updateCustomerStatus(callback: (JsonObject) -> Unit) {
        GPDRecurrenceCustomer.updateStatus(
            authKey = authKey,
            sellerId = sellerId,
            customerId = edtCustomerId2.text.toString(),
            status = GPDCustomerStatus.valueOf(spStatus.selectedItem.toString().toUpperCase())
        ) { response ->
            callback(response)
        }
    }

    private fun startResponseFragment(response : String) {
        val bundle = Bundle()
        loadingDialog?.dismissLoadingDialog()
        bundle.putString("response", response)
        findNavController().navigate(R.id.action_recurrenceCustomerFormFragment2_to_ResponseFragment, bundle)
    }
}