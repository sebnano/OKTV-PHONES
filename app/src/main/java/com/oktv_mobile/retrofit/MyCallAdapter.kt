package com.oktv_mobile.retrofit

import android.util.Log
import android.widget.Toast
import com.oktv_mobile.OkTv
import com.oktv_mobile.utils.ConnectivityCheckingClass
import com.oktv_mobile.utils.Constant
import com.oktv_mobile.utils.MyPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCallAdapter<T>(private val call : Call<T>) : MyCall<T> {

    private var isInternetStatusShow = false

    override fun enqueue(callback: MyCallback<T>) {
        if (!isInternetStatusShow) {
            if (!ConnectivityCheckingClass.isNetworkAvailable(OkTv.context)) {
                Toast.makeText(OkTv.context,"No Internet Connection",Toast.LENGTH_SHORT).show()
                isInternetStatusShow = true
                callback.networkError()
                return
            }
        }

        Log.i("REQUEST_BODY",call.request().toString())

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                try {
                    when {
                        response.code() == 200 -> {
                            Log.v("API_RESPONSE", "$response")
                            callback.success(response)
                        }
                        response.code() == 201 -> {
                            Log.v("API_RESPONSE", "$response")
                            callback.success(response)
                        }
                        response.code() == 204  -> {
                            Log.v("API_RESPONSE", "$response")
                            callback.success(response)
                        }
                        response.code() == 401 -> {
                            Log.v("API_RESPONSE", "${response.errorBody()!!}")
                            callback.authError()
                            val fcmToken = MyPreferences.getFromPreferences(OkTv.context, Constant.FCM_TOKEN)
                            MyPreferences.clearData(OkTv.context)
                            MyPreferences.saveStringInPreference(OkTv.context, Constant.FCM_TOKEN, fcmToken.toString())

//                            GuessBay.context.startActivity(Intent(GuessBay.context, LoginActivity::class.java)
//                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                        response.code() == 403 -> {
                            callback.authError()
                           /* val fcmToken = MyPreferences.getFromPreferences(HAW.context, Constant.FCM_TOKEN)
                            MyPreferences.clearData(HAW.context)
                            MyPreferences.saveStringInPreference(HAW.context, Constant.FCM_TOKEN, fcmToken.toString())*/

//                            GuessBay.context.startActivity(Intent(GuessBay.context, LoginActivity::class.java)
//                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                        else -> {
                            Log.v("API_RESPONSE", "${response.errorBody()!!.byteString()}")
                            callback.serverError()
                        }
                    }
                } catch (e: Exception) {
                    Log.v("API_RESPONSE", "api exception ${e.message}")
                    callback.serverError()
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.v("API_RESPONSE", "api failure ${t.message}")
                callback.serverError()
            }
        })
    }

    override fun cancel() {
        call.cancel()
    }
}