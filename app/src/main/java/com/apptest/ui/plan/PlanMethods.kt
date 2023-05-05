package com.apptest.ui.plan

import com.apptest.R
import com.apptest.model.Methods

class PlanMethods {
    fun getPlanMethods(): MutableList<Methods> {

        return mutableListOf(
            Methods(R.string.key_new_plan, "Criar plano"),
            Methods(R.string.key_list_plans, "Listar planos"),
            Methods(R.string.key_get_plan, "Detalhe plano"),
            Methods(R.string.key_update_plan, "Editar plano"),
            Methods(R.string.key_update_plan_status, "Editar status do plano"),
            Methods(R.string.key_migrate_plan, "Migrar plano"),
            Methods(R.string.key_simulate_plan, "Retorna uma simulação de cobrança, data e valor para o plano informado"),
            Methods(R.string.key_list_plan_by_filter, "Retorna a lista de planos de recorrência cadastradas com base nos filtros escolhidos"),
            Methods(R.string.key_alter_plan, "Cria alteração de plano para assinatura")

            )
    }
}