package com.apptest.ui.payments.debit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class DebitViewModel: ViewModel() {

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsCredit(){
        _listMethods.value = DebitMethods().getDebitMethods()
    }
}