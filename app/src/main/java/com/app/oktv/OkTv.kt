package com.app.oktv

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.app.oktv.utils.Constant
import com.app.oktv.utils.MyPreferences

@SuppressLint("StaticFieldLeak")
class OkTv : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        if (MyPreferences.getFromPreferences(this, Constant.TOKEN).toString().isNotEmpty()) {
            Constant.USERTOKEN = MyPreferences.getFromPreferences(this, Constant.TOKEN).toString()
        }
    }
}


//https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_evEo7iGcecrowVQQepa7bxiP6IfOSv33n5FbeYH1&currencies=EUR&base_currency=INR