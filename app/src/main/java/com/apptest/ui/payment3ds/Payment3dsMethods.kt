package com.apptest.ui.payment3ds

import com.apptest.R
import com.apptest.model.Methods

class Payment3dsMethods {
    fun getPayment3dsMethods(): MutableList<Methods> {

        return mutableListOf(
            Methods(R.string.key_payment_authenticated, "Pagamento autenticado 3ds"),
            Methods(R.string.key_payment_confirm, "Confirmação tardia de pagamento ou confirmação de pré autorização com cartão de crédito com autenticação 3DS."),
            Methods(R.string.key_payment_adjustment, "Ajustar valor de uma transação de pré autorização com autenticação 3DS"),
            Methods(R.string.key_payment_cancel, "Cancelar pagamento no cartão de crédito com autenticação 3DS")
        )
    }
}