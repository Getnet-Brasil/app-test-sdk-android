package com.apptest.ui.token

import com.apptest.R
import com.apptest.model.Methods

class TokenMethods {
    fun getTokenMethods(): MutableList<Methods> {
        return mutableListOf(
            Methods(R.string.key_get_card_number_token, "Tokenização de cartão"),
        )
    }
}