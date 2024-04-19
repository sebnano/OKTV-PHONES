package com.oktv_mobile.retrofit

interface MyCall<T> {

    fun cancel()

    fun enqueue(callback: MyCallback<T>)

}