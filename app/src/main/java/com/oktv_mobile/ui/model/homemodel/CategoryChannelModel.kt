package com.oktv_mobile.ui.model.homemodel

import com.google.gson.annotations.SerializedName

data class CategoryChannelModel(
    @SerializedName("category")
    val category: String,
    @SerializedName("id_category")
    val idCategory: String,
    var categoryList: ArrayList<ChannelDataModel> = ArrayList<ChannelDataModel>()
)
