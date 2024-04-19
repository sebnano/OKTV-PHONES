package com.oktv_mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.oktv_mobile.retrofit.MyViewModel
import com.oktv_mobile.retrofit.ResponseStatus
import com.oktv_mobile.ui.repo.HomeRepo
import com.oktv_mobile.ui.model.homemodel.*

class HomeVM  : MyViewModel() {
    private lateinit var homeRepo: HomeRepo

    private var homeBannerModel = MutableLiveData<ArrayList<HomeBannerModel>>()
    fun observeBanner() = homeBannerModel
    fun banner(id : String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.homeBanner(id).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                homeBannerModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var categoryChannelModel = MutableLiveData<ArrayList<CategoryChannelModel>>()
    fun observeChannelCategory() = categoryChannelModel
    fun channelCategory(id : String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.channelCategory(id).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                Log.e("111111","channelCategory : "+it.data)
                categoryChannelModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var channelModel = MutableLiveData<ArrayList<ChannelDataModel>>()
    fun observeAllChannels() = channelModel
    fun allChannelList(id : String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.channelData(id).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                channelModel.value = it.data
            } else{
                errorListener.value = it.status
                Log.e("message","+${it.status}")
            }
            isLoading.value = false
        }
    }

    private var channelPerCategoryModel = MutableLiveData<ArrayList<ChannelDataModel>>()
    fun observeChannelPerCategory() = channelPerCategoryModel
    fun channelListPerCategory(operatorId : String,channelId : String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.channelPerCategory(operatorId,channelId).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                channelPerCategoryModel.value = it.data
            } else {
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var deviceModel = MutableLiveData<ArrayList<UserDeviceModel>>()
    fun observeDevices() = deviceModel
    fun userDeviceList(userId: String,operatorId: String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.userDevices(userId,operatorId).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                deviceModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var favouriteModel = MutableLiveData<ArrayList<ChannelDataModel>>()
    fun observeFavourite() = favouriteModel
    fun getUserFavourite(userId : String,operatorId: String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.userFavourite(userId,operatorId).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                favouriteModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var addNewDevice = MutableLiveData<AddCreatedDeviceModel>()
    fun observeAddConnectedDevice() = addNewDevice
    fun addNewConnectedDevice(body : HashMap<String,Any>) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.addConnectedDevice(body).observeForever{
            if (it.status == ResponseStatus.SUCCESS || it.status == ResponseStatus.AUTH_ERROR) {
                Log.e("AddedNewDevice","---------->VM1"+it.message)
                addNewDevice.value = it.data
            } else {
                Log.e("AddedNewDevice","---------->VM2"+it.status)
                errorListener.value = it.status
            }
        }
    }

    private var logOutModel = MutableLiveData<Unit>()
    fun observeLogOut() = logOutModel
    fun logOut(token : String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.logOut(token).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                logOutModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var markFavouriteChanelModel = MutableLiveData<MarkFavouriteModel>()
    fun observeMarkFavouriteChanel() = markFavouriteChanelModel
    fun markFavouriteChanel(body: HashMap<String,Any>) {
        Log.e("BODYMAP", body.toString())
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.markFavouriteChanel(body).observeForever{
            if (it.status == ResponseStatus.SUCCESS) {
                markFavouriteChanelModel.value = it.data
            } else{
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }

    private var deleteDeviceModel = MutableLiveData<Unit>()
    fun observeDeleteDevice() = deleteDeviceModel
    fun deleteUserDevice(nodeId : String) {
        if (!this::homeRepo.isInitialized) {
            homeRepo = HomeRepo()
        }
        isLoading.value = true
        homeRepo.deleteDevices(nodeId).observeForever{
            if (it.status == ResponseStatus.SUCCESS || it.status == ResponseStatus.AUTH_ERROR) {
                Log.e("AddedNewDevice","---------->DeleteVM1"+it.data)
                deleteDeviceModel.value = it.data
            } else{
                Log.e("AddedNewDevice","---------->DeleteVM2"+it.status)
                errorListener.value = it.status
            }
            isLoading.value = false
        }
    }
}