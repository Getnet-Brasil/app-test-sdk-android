package com.apptest.ui.plan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class PlanViewModel: ViewModel() {

    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsPlan(){
        _listMethods.value = PlanMethods().getPlanMethods()
    }
}