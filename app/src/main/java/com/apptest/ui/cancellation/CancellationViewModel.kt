package com.apptest.ui.cancellation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class CancellationViewModel : ViewModel(){

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsCancellation(){
        _listMethods.value = CancellationMethods().getCancellationMethods()
    }
}