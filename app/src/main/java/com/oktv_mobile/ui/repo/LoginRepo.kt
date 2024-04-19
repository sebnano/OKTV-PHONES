package com.oktv_mobile.ui.repo

import androidx.lifecycle.MutableLiveData
import com.oktv_mobile.retrofit.MyCallback
import com.oktv_mobile.retrofit.MyResponse
import com.oktv_mobile.retrofit.MyRetrofit
import com.oktv_mobile.ui.`interface`.LoginApiCall
import com.oktv_mobile.ui.model.loginmodel.*
import okhttp3.RequestBody
import retrofit2.Response

class LoginRepo {

    fun configurationRepo(): MutableLiveData<MyResponse<ArrayList<ConfiguracionModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<ConfiguracionModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(LoginApiCall::class.java)
        val call = retrofitService.getConfiguration()

        call.enqueue(object : MyCallback<ArrayList<ConfiguracionModel>> {
            override fun success(response: Response<ArrayList<ConfiguracionModel>>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError("invalid"))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError("invalid"))
            }
        })
        return data
    }

    fun login(body: HashMap<String,Any>): MutableLiveData<MyResponse<LoginModel>> {
        val data = MutableLiveData<MyResponse<LoginModel>>()
        val retrofitService = MyRetrofit.getRetrofitService(LoginApiCall::class.java)
        val call = retrofitService.login(body)

        call.enqueue(object : MyCallback<LoginModel> {
            override fun success(response: Response<LoginModel>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }
        })
        return data
    }

    fun authorization(body: RequestBody): MutableLiveData<MyResponse<AuthTokenModel>> {
        val data = MutableLiveData<MyResponse<AuthTokenModel>>()
        val retrofitService = MyRetrofit.getRetrofitService(LoginApiCall::class.java)
        val call = retrofitService.authToken(body)

        call.enqueue(object : MyCallback<AuthTokenModel> {
            override fun success(response: Response<AuthTokenModel>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }
        })
        return data
    }

    fun userOperator(userId : String): MutableLiveData<MyResponse<ArrayList<UserOperatorModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<UserOperatorModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(LoginApiCall::class.java)
        val call = retrofitService.userOperator(userId)

        call.enqueue(object : MyCallback<ArrayList<UserOperatorModel>> {
            override fun success(response: Response<ArrayList<UserOperatorModel>>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }
        })
        return data
    }

    fun languageName(): MutableLiveData<MyResponse<ArrayList<ConfiguracionModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<ConfiguracionModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(LoginApiCall::class.java)
        val call = retrofitService.getLanguageName()

        call.enqueue(object : MyCallback<ArrayList<ConfiguracionModel>> {
            override fun success(response: Response<ArrayList<ConfiguracionModel>>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }
        })
        return data
    }

    fun languageString(languageCode : String): MutableLiveData<MyResponse<ArrayList<LanguageStringModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<LanguageStringModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(LoginApiCall::class.java)
        val call = retrofitService.getLanguageString(languageCode)

        call.enqueue(object : MyCallback<ArrayList<LanguageStringModel>> {
            override fun success(response: Response<ArrayList<LanguageStringModel>>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }
        })
        return data
    }
}