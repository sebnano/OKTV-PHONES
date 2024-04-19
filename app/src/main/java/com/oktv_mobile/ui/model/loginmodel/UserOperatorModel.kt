package com.oktv_mobile.ui.model.loginmodel

import com.google.gson.annotations.SerializedName

data class UserOperatorModel (
    @SerializedName("operador")
    val operador: String,
    @SerializedName("nombre_operador")
    val operatorName: String,
    @SerializedName("max_devices")
    var maxDevices: String
)
