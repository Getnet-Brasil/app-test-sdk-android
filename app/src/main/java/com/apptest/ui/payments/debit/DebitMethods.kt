package com.apptest.ui.payments.debit

import com.apptest.R
import com.apptest.model.Methods

class DebitMethods {
    fun getDebitMethods(): MutableList<Methods> {
        return mutableListOf(
            Methods(R.string.key_payment_debit, "Pagamento com cartão de débito"),
            Methods(R.string.key_payment_debit_cancel, "Cancelar pagamento no cartão de debito"),
        )
    }
}