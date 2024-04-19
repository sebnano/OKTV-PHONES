package com.oktv_mobile.ui.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.oktv_mobile.retrofit.MyCallback
import com.oktv_mobile.retrofit.MyResponse
import com.oktv_mobile.retrofit.MyRetrofit
import com.oktv_mobile.ui.`interface`.HomeApiCall
import com.oktv_mobile.ui.model.homemodel.*
import retrofit2.Response

class HomeRepo {

    fun homeBanner(id: String): MutableLiveData<MyResponse<ArrayList<HomeBannerModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<HomeBannerModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.homeBanner(id)

        call.enqueue(object : MyCallback<ArrayList<HomeBannerModel>> {
            override fun success(response: Response<ArrayList<HomeBannerModel>>) {
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

    fun channelCategory(id: String): MutableLiveData<MyResponse<ArrayList<CategoryChannelModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<CategoryChannelModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.channelCategory(id)

        call.enqueue(object : MyCallback<ArrayList<CategoryChannelModel>> {
            override fun success(response: Response<ArrayList<CategoryChannelModel>>) {

                Log.e("channelCategory:- ", "" + response.body())
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

    fun channelData(id: String): MutableLiveData<MyResponse<ArrayList<ChannelDataModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<ChannelDataModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.channels(id)

        call.enqueue(object : MyCallback<ArrayList<ChannelDataModel>> {
            override fun success(response: Response<ArrayList<ChannelDataModel>>) {
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

    fun channelPerCategory(
        operatorId: String,
        channelId: String
    ): MutableLiveData<MyResponse<ArrayList<ChannelDataModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<ChannelDataModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.channelPerCategory(operatorId, channelId)

        call.enqueue(object : MyCallback<ArrayList<ChannelDataModel>> {
            override fun success(response: Response<ArrayList<ChannelDataModel>>) {
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

    fun userDevices(
        userId: String,
        operatorId: String
    ): MutableLiveData<MyResponse<ArrayList<UserDeviceModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<UserDeviceModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.getUserDevice(userId, operatorId)

        call.enqueue(object : MyCallback<ArrayList<UserDeviceModel>> {
            override fun success(response: Response<ArrayList<UserDeviceModel>>) {
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

    fun addConnectedDevice(body: HashMap<String, Any>): MutableLiveData<MyResponse<AddCreatedDeviceModel>> {
        val data = MutableLiveData<MyResponse<AddCreatedDeviceModel>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.addConnectedDevice(body)

        call.enqueue(object : MyCallback<AddCreatedDeviceModel> {
            override fun success(response: Response<AddCreatedDeviceModel>) {
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

    fun userFavourite(
        userId: String,
        operatorId: String
    ): MutableLiveData<MyResponse<ArrayList<ChannelDataModel>>> {
        val data = MutableLiveData<MyResponse<ArrayList<ChannelDataModel>>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.getFavourite(userId, operatorId)

        call.enqueue(object : MyCallback<ArrayList<ChannelDataModel>> {
            override fun success(response: Response<ArrayList<ChannelDataModel>>) {
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

    fun logOut(token: String): MutableLiveData<MyResponse<Unit>> {
        val data = MutableLiveData<MyResponse<Unit>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.logOut(token)

        call.enqueue(object : MyCallback<Unit> {
            override fun success(response: Response<Unit>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

        })

        return data
    }

    fun markFavouriteChanel(body: HashMap<String, Any>): MutableLiveData<MyResponse<MarkFavouriteModel>> {
        val data = MutableLiveData<MyResponse<MarkFavouriteModel>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.markFavouriteChanel(body)

        call.enqueue(object : MyCallback<MarkFavouriteModel> {
            override fun success(response: Response<MarkFavouriteModel>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

        })
        return data
    }

    fun deleteDevices(nodeId: String): MutableLiveData<MyResponse<Unit>> {
        val data = MutableLiveData<MyResponse<Unit>>()
        val retrofitService = MyRetrofit.getRetrofitService(HomeApiCall::class.java)
        val call = retrofitService.deleteDevice(nodeId)

        call.enqueue(object : MyCallback<Unit> {
            override fun success(response: Response<Unit>) {
                data.postValue(MyResponse.success(response.body()!!))
            }

            override fun serverError() {
                data.postValue(MyResponse.authError(""))
            }

            override fun networkError() {
                data.postValue(MyResponse.internetError())
            }

            override fun authError() {
                data.postValue(MyResponse.authError(""))
            }

        })

        return data
    }
}
