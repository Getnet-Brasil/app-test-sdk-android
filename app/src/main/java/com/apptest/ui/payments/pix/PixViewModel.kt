package com.apptest.ui.payments.pix

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class PixViewModel: ViewModel(){

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsPix(){
        _listMethods.value = PixMethods().getPixMethods()
    }
}