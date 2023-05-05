package com.apptest.ui.subscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class SubscriptionViewModel : ViewModel() {


    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsSubscription(){
      _listMethods.value = SubscriptionMethods().getSubscriptionMethods()
    }
}