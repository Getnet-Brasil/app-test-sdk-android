package com.apptest.ui.cards

import com.apptest.R
import com.apptest.model.Methods

class CardsMethods {
    fun getCardsMethods(): MutableList<Methods> {
        return mutableListOf(
            Methods(R.string.key_vault_add_card, "Armazena os dados de cartão do consumidor no cofre"),
            Methods(R.string.key_vault_list_cards, "Lista de cartões salvos no cofre"),
            Methods(R.string.key_vault_get_card, "Recupera um cartão salvo no cofre"),
            Methods(R.string.key_vault_remove_card, "Remove um cartão salvo no cofre"),
        )
    }
}