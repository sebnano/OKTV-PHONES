package com.oktv_mobile.retrofit

import android.util.Log
import com.oktv_mobile.OkTv
import com.oktv_mobile.utils.Constant
import com.oktv_mobile.utils.MyPreferences
import com.intuit.sdp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyRetrofit {

    companion object {
        private const val baseUrl: String = "https://portal.ok-television.com/"

        private lateinit var client: OkHttpClient

        private val headerInterceptor = Interceptor { chain ->
            Log.i("API_RESPONSE", "headerInterceptor called")
            var request = chain.request()

            request = if(MyPreferences.getFromPreferences(OkTv.context, Constant.ACCESSTOKEN).isNotEmpty()) {
                request.newBuilder()
                        .addHeader("Authorization", "Bearer ${Constant.USERTOKEN}")
                        .build()
            } else {
                request.newBuilder()
                        .build()
            }
            chain.proceed(request)
        }


        private fun getRetrofit() = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(CustomCallAdapter())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build()

        private fun getHttpClient(): OkHttpClient {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            client = if (BuildConfig.DEBUG) {
                OkHttpClient.Builder().addInterceptor(headerInterceptor)
                        .addInterceptor(interceptor)
                        .connectTimeout(60, TimeUnit.MINUTES)
                        .readTimeout(60, TimeUnit.MINUTES)
                        .writeTimeout(60, TimeUnit.MINUTES).build()
            } else {
                OkHttpClient.Builder().addInterceptor(headerInterceptor)
                        .addInterceptor(interceptor)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS).build()
            }
            return client
        }

        fun <T> getRetrofitService(serviceType : Class<T>): T {
            return getRetrofit().create(serviceType)
        }
    }
}