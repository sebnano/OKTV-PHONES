package com.oktv_mobile.ui.model.loginmodel

import com.google.gson.annotations.SerializedName

data class LanguageStringModel(
    @SerializedName("field_key")
    val key: String,
    @SerializedName("field_value")
    val value: String
)
