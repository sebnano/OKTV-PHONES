package com.app.guessbay.retrofit

import retrofit2.Response


interface MyCallback<T> {

    fun success(response: Response<T>)

    fun serverError()

    fun networkError()

    fun authError()

}