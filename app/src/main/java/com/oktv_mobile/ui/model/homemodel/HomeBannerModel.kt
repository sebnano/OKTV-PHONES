package com.oktv_mobile.ui.model.homemodel

import com.google.gson.annotations.SerializedName

data class HomeBannerModel(
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("field_active_banner")
    val field_active_banner: String,
    @SerializedName("field_imagen")
    val fieldImage: String,
    @SerializedName("field_position")
    val fieldPosition: String,
    @SerializedName("field_automatic_slide")
    val field_automatic_slide: String
)
