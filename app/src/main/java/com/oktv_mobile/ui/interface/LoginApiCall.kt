package com.oktv_mobile.ui.`interface`

import com.oktv_mobile.retrofit.MyCall
import com.oktv_mobile.ui.model.loginmodel.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApiCall {

    @POST("user/login?_format=json")
    fun login(@Body body: HashMap<String,Any>) : MyCall<LoginModel>

    @GET("api/configuracion-json/parametros-basicos?_format=json")
    fun getConfiguration() : MyCall<ArrayList<ConfiguracionModel>>

    @POST("oauth/token")
    fun authToken(@Body body: RequestBody) : MyCall<AuthTokenModel>

    @GET("json_app_oktv/operadores_del_usuario_conectado/{userId}?_format=json")
    fun userOperator(@Path("userId") userId: String) : MyCall<ArrayList<UserOperatorModel>>

    @GET("api/configuracion-json/idiomas?_format=json")
    fun getLanguageName() : MyCall<ArrayList<ConfiguracionModel>>

    @GET("api/dictionary/phone/{languageCode}?_format=json")
    fun getLanguageString(@Path("languageCode") languageCode: String) : MyCall<ArrayList<LanguageStringModel>>
}