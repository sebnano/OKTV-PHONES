package com.oktv_mobile

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.oktv_mobile.utils.Constant
import com.oktv_mobile.utils.DownloadManager
import com.oktv_mobile.utils.MyPreferences

@SuppressLint("StaticFieldLeak")
class OkTv : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        if (MyPreferences.getFromPreferences(this, Constant.ACCESSTOKEN).toString().isNotEmpty()) {
            Constant.USERTOKEN = MyPreferences.getFromPreferences(this, Constant.ACCESSTOKEN).toString()
        }
    }
}


//https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_evEo7iGcecrowVQQepa7bxiP6IfOSv33n5FbeYH1&currencies=EUR&base_currency=INR