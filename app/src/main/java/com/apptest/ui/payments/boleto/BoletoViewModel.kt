package com.apptest.ui.payments.boleto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class BoletoViewModel : ViewModel() {


    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsBoleto(){
      _listMethods.value = BoletoMethods().getBoletoMethods()
    }
}