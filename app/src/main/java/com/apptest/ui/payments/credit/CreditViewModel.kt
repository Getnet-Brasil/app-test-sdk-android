package com.apptest.ui.payments.credit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class CreditViewModel: ViewModel() {

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsCredit(){
        _listMethods.value = CreditMethods().getCreditMethods()
    }
}