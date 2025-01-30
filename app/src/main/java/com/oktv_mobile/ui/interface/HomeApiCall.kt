package com.oktv_mobile.ui.`interface`

import com.oktv_mobile.retrofit.MyCall
import com.oktv_mobile.ui.model.homemodel.*
import retrofit2.Call
import retrofit2.http.*

interface HomeApiCall {
    @GET("json_app_oktv/banners_phones/{operatorId}")
    fun homeBanner(@Path("operatorId") operatorId: String) : MyCall<ArrayList<HomeBannerModel>>

    @GET("json_app_oktv/categorias-canales/{operatorId}?_format=json")
    fun channelCategory(@Path("operatorId") operatorId: String) : MyCall<ArrayList<CategoryChannelModel>>

    @GET("json_app_oktv/canales/{operatorId}?_format=json")
    fun channels(@Path("operatorId") operatorId: String) : MyCall<ArrayList<ChannelDataModel>>

    @GET("json_app_oktv/canales-by-category/{operatorId}/{channelId}?_format=json")
    fun channelPerCategory(@Path("operatorId") operatorId: String,@Path("channelId") channelId: String) : MyCall<ArrayList<ChannelDataModel>>

    @GET("api/dispositivos-por-usuarios/{userId}/{operatorId}?_format=json")
    fun getUserDevice(@Path("userId") userId: String,@Path("operatorId") operatorId: String) : MyCall<ArrayList<UserDeviceModel>>

    @GET("json_app_oktv/canales_favoritos_usuario/{userId}/{operatorId}?_format=json")
    fun getFavourite(@Path("userId") userId: String,@Path("operatorId") channelId: String) : MyCall<ArrayList<ChannelDataModel>>

    @GET("user/logout")
    fun logOut(@Query("token") userId: String) : MyCall<Unit>

    @POST("node?_format=json")
    fun addConnectedDevice(@Body body: HashMap<String,Any>) : MyCall<AddCreatedDeviceModel>

    @POST("node?_format=json")
    fun markFavouriteChanel(@Body body: HashMap<String,Any>) : MyCall<MarkFavouriteModel>

    @DELETE("node/{node_id}?_format=json")
    fun deleteDevice1(@Path("node_id") nodeId: String) : Call<Unit>

    @DELETE("node/{node_id}?_format=json")
    fun deleteDevice(@Path("node_id") nodeId: String) : MyCall<Unit>
}
