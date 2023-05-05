package com.apptest.ui.cards

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class CardsViewModel : ViewModel(){

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsCustomer(){
        _listMethods.value = CardsMethods().getCardsMethods()
    }
}