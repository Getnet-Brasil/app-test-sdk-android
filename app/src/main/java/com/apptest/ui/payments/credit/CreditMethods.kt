package com.apptest.ui.payments.credit

import com.apptest.R
import com.apptest.model.Methods

class CreditMethods {
    fun getCreditMethods(): MutableList<Methods> {
        return mutableListOf(
            Methods(R.string.key_payment_credit_verify_card, "Verificação de cartão"),
            Methods(R.string.key_payment_credit, "Pagamento com cartão de crédito"),
            Methods(R.string.key_payment_credit_confirmation, "Confirmação tardia de pagamento ou confirmação de pré autorização com cartão de crédito"),
            Methods(R.string.key_payment_credit_adjust, "Ajustar valor de uma transação de pré autorização"),
            Methods(R.string.key_payment_credit_cancel, "Cancelar pagamento no cartão de crédito"),
        )
    }
}
