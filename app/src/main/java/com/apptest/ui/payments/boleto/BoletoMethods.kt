package com.apptest.ui.payments.boleto

import com.apptest.R
import com.apptest.model.Methods

class BoletoMethods {

    fun getBoletoMethods(): MutableList<Methods> {

        return mutableListOf(
                Methods(R.string.key_create_boleto_v1, "Registro de pagamento com boleto"),
                Methods(R.string.key_download_boleto_pdf_v1, "Download pdf do boleto"),
                Methods(R.string.key_download_boleto_html_v1, "Download html do boleto"),
            )

    }
}