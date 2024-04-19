package com.oktv_mobile.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.oktv_mobile.retrofit.MyViewModel
import com.oktv_mobile.retrofit.ResponseStatus
import com.oktv_mobile.ui.repo.LoginRepo
import com.oktv_mobile.ui.model.loginmodel.*
import okhttp3.MultipartBody

class LoginVM : MyViewModel() {
    private lateinit var loginRepo: LoginRepo

    private var configurationModel = MutableLiveData<ArrayList<ConfiguracionModel>>()
    fun observeConfiguration() = configurationModel
    fun configuration() {
        if (!this::loginRepo.isInitialized) {
            loginRepo = LoginRepo()
        }
        isLoading.value = true
        loginRepo.configurationRepo().observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                configurationModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var loginUserModel = MutableLiveData<LoginModel?>()
    fun observeLogin() = loginUserModel
    fun login(body : HashMap<String,Any>) {
        if (!this::loginRepo.isInitialized) {
            loginRepo = LoginRepo()
        }
        isLoading.value = true
        loginRepo.login(body).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                loginUserModel.value = it.data
            } else{
                errorListener.value = it.status
                loginUserModel.value = it.data
            }
            isLoading.value = false
        }
    }

    private var authTokenModel = MutableLiveData<AuthTokenModel>()
    fun observeAuth() = authTokenModel
    fun authentication(userName : String,password : String) {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        builder.addFormDataPart("grant_type", "password")
        builder.addFormDataPart("client_id", "MdO6QONxC_fI0xvbRq7wLZxa6Q1cDYHh7xPkYcdLy_Q")
        builder.addFormDataPart("client_secret", "cliente_simple_oauth2023OKTV")
        builder.addFormDataPart("username", userName)
        builder.addFormDataPart("password", password)

        val requestBody = builder.build()

        if (!this::loginRepo.isInitialized) {
            loginRepo = LoginRepo()
        }
        isLoading.value = true
        loginRepo.authorization(requestBody).observeForever {
            if (it.status == ResponseStatus.SUCCESS) {
                authTokenModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var operatorModel = MutableLiveData<ArrayList<UserOperatorModel>>()
    fun observeOperator() = operatorModel
    fun operator(userId : String) {
        if (!this::loginRepo.isInitialized) {
            loginRepo = LoginRepo()
        }
        isLoading.value = true
        loginRepo.userOperator(userId).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                operatorModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var languageNameModel = MutableLiveData<ArrayList<ConfiguracionModel>>()
    fun observeLanguageName() = languageNameModel
    fun getLanguageNameList() {
        if (!this::loginRepo.isInitialized) {
            loginRepo = LoginRepo()
        }
        isLoading.value = true
        loginRepo.languageName().observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                languageNameModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var languageStringModel = MutableLiveData<ArrayList<LanguageStringModel>>()
    fun observeLanguageString() = languageStringModel
    fun getLanguageStringList(languageCode : String) {
        if (!this::loginRepo.isInitialized) {
            loginRepo = LoginRepo()
        }
        isLoading.value = true
        loginRepo.languageString(languageCode).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                languageStringModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }
}