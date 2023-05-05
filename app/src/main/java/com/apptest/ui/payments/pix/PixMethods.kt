package com.apptest.ui.payments.pix

import com.apptest.R
import com.apptest.model.Methods

class PixMethods {
    fun getPixMethods(): MutableList<Methods> {
        return mutableListOf(
            Methods(R.string.key_generate_pix, "Geração de QR Code para pagamento PIX"),
            //Methods(R.string.key_check_payment_pix_status, "Consulta status do pagamento via PIX")
        )
    }
}