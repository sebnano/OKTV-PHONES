package com.oktv_mobile.ui.model.loginmodel

import com.google.gson.annotations.SerializedName

data class ConfiguracionModel (
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("image")
    val image: String
)
