package com.apptest.ui.authentication3ds

import com.apptest.R
import com.apptest.model.Methods

class Authentication3dsMethods {
    fun getAuthentication3dsMethods(): MutableList<Methods> {

        return mutableListOf(
            Methods(R.string.key_card_brand, "Retorna a bandeira do cartão"),
            Methods(R.string.key_get_card_number_token, "Tokenização de cartão"),

            Methods(R.string.key_generate_token, "Gerar token de acesso 3ds"),
            Methods(R.string.key_get_authentications, "Solicitação de autenticação 3ds"),
            Methods(R.string.key_get_authentications_result, "Solicitação de resultado da autenticação 3ds"),
        )
    }
}