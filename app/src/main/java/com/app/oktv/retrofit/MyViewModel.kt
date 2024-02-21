package com.app.guessbay.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class MyViewModel : ViewModel() {

    var isLoading = MutableLiveData(false)
    var errorListener = MutableLiveData<ResponseStatus>()

    fun observeLoading() = isLoading

}