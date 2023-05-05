package com.apptest.ui.customer

import com.apptest.R
import com.apptest.model.Methods

class CustomerMethods {
    fun getCustomerMethods(): MutableList<Methods> {

        return mutableListOf(
            Methods(R.string.key_new_customer, "Criar cliente"),
            Methods(R.string.key_list_customers, "Listar clientes"),
            Methods(R.string.key_get_customer, "Detalhe cliente"),
            Methods(R.string.key_update_customer, "Editar cliente"),
            Methods(R.string.key_update_customer_status, "Editar status de cliente"),
        )
    }
}