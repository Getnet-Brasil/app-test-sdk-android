package com.apptest.ui.token

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class TokenViewModel : ViewModel() {

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsToken(){
        _listMethods.value = TokenMethods().getTokenMethods()
    }
}