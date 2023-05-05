package com.apptest.ui.cancellation

import com.apptest.R
import com.apptest.model.Methods

class CancellationMethods {
    fun getCancellationMethods(): MutableList<Methods> {
        return mutableListOf(
            Methods(R.string.key_cancellation_request, "Solicita um cancelamento"),
            Methods(R.string.key_get_cancellation_by_customer, "Retorna uma solicitação de cancelamento pela chave do cliente"),
            Methods(R.string.key_get_cancellation_by_id, "Retorna uma solicitação de cancelamento pelo identificador"),
        )
    }
}