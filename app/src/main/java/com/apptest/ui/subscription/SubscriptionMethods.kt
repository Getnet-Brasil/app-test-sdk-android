package com.apptest.ui.subscription

import com.apptest.R
import com.apptest.model.Methods

class SubscriptionMethods {

    fun getSubscriptionMethods(): MutableList<Methods> {

        return mutableListOf(
                Methods(R.string.key_new_subscription, "Cadastra uma nova assinatura"),
                Methods(R.string.key_list_subscriptions, "Lista de assinaturas"),
                Methods(R.string.key_get_subscription, "Assinatura de recorrência"),
                Methods(R.string.key_cancel_subscription, "Cancela uma assinatura de recorrência"),
                Methods(R.string.key_billing_projection, "Demonstra a projeção de cobranças"),
                Methods(R.string.key_confirm_payment, "Confirma o pagamento da assinatura"),
                Methods(R.string.key_update_date_charge, "Atualiza data da cobrança"),
                Methods(R.string.key_update_data_payment, "Atualiza os dados de pagamento"),
                Methods(R.string.key_register_change_date_value, "Cadastra uma alteração de data ou valor para a assinatura"),
                Methods(R.string.key_list_changes, "Lista todas as alterações de data e/ou valor de uma assinatura de recorrência"),
                Methods(R.string.key_remove_changes, "Remove todas as alterações de data e valor de uma assinatura"),
                Methods(R.string.key_add_new_change, "Adiciona novas alterações de data e/ou valor para cobranças de uma assinatura"),
                Methods(R.string.key_delete_change, "Remove a alteração de data e valor para uma cobrança específica de uma assinatura"),
                Methods(R.string.key_update_date_or_value, "Atualiza a data e valor para uma cobrança específica de uma assinatura"),
                Methods(R.string.key_alter_plan_subscription, "Alteração do Plano de uma Assinatura"),
                Methods(R.string.key_list_charges, "Lista de cobranças"),
                Methods(R.string.key_cancel_subscription_by_seller_id, "Cancela todas as assinaturas por sellerID"),
                Methods(R.string.key_get_subscription_by_plan, "Listar assinatura por plano"),
                Methods(R.string.key_alter_subscription, "Realiza a atualização da assinatura de recorrência")
            )

    }
}