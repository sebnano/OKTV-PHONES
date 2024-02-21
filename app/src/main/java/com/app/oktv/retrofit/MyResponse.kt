package com.app.guessbay.retrofit

data class MyResponse<out T>(val status: ResponseStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): MyResponse<T> {
            return MyResponse(ResponseStatus.SUCCESS, data, null)
        }

        fun <T> serverError(): MyResponse<T> {
            return MyResponse(ResponseStatus.SERVER_ERROR, null, null)
        }

        fun <T> internetError(): MyResponse<T> {
            return MyResponse(ResponseStatus.INTERNET_NOT_AVAILABLE, null, null)
        }

        fun <T> authError(msg: String): MyResponse<T> {
            return MyResponse(ResponseStatus.AUTH_ERROR, null, msg)
        }
    }
}