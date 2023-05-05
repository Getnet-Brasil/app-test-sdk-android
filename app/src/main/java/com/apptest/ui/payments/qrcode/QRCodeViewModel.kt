package com.apptest.ui.payments.qrcode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class QRCodeViewModel : ViewModel() {


    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsSubscription(){
      _listMethods.value = QRCodeMethods().getQRCodeMethods()
    }
}