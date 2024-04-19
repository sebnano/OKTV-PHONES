package com.oktv_mobile.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oktv_mobile.retrofit.ResponseStatus

open class MyViewModel : ViewModel() {

    var isLoading = MutableLiveData(false)
    var errorListener = MutableLiveData<ResponseStatus>()

    fun observeLoading() = isLoading

}