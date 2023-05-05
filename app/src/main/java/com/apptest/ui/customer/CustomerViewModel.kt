package com.apptest.ui.customer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class CustomerViewModel : ViewModel(){

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsCustomer(){
        _listMethods.value = CustomerMethods().getCustomerMethods()
    }
}