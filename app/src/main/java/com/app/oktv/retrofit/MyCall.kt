package com.app.guessbay.retrofit

interface MyCall<T> {

    fun cancel()

    fun enqueue(callback: MyCallback<T>)

}