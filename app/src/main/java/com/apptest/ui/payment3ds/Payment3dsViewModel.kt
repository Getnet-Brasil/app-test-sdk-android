package com.apptest.ui.payment3ds

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class Payment3dsViewModel : ViewModel() {
    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsPayment3ds(){
        _listMethods.value = Payment3dsMethods().getPayment3dsMethods()
    }
}