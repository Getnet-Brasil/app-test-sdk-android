package com.apptest.ui.payments.qrcode

import com.apptest.R
import com.apptest.model.Methods

class QRCodeMethods {

    fun getQRCodeMethods(): MutableList<Methods> {
        return mutableListOf(
            //Methods(R.string.key_check_payment_pix_status, "Consulta status do pagamento pix"),
            Methods(R.string.key_generate_qrcode, "Geração de QRCode para pagamento"),
            Methods(R.string.key_check_payment_status, "Consulta status do pagamento")
        )
    }
}