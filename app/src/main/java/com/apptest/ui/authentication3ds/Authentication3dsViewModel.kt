package com.apptest.ui.authentication3ds

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apptest.model.Methods

class Authentication3dsViewModel : ViewModel() {
    private val _listMethods : MutableLiveData<List<Methods>> = MutableLiveData()
    val listMethods = _listMethods

    fun getMethodsAuthentication3ds(){
        _listMethods.value = Authentication3dsMethods().getAuthentication3dsMethods()
    }
}