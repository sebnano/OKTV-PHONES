package com.oktv_mobile.ui.model.homemodel

import com.google.gson.annotations.SerializedName

data class UserDeviceModel(
    @SerializedName("field_dispositivo_nombre")
    val deviceTitle: String,
    @SerializedName("field_dispositivo_ip")
    val deviceIp: String,
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("field_dispositivo_mac")
    val deviceMac: String,
    @SerializedName("field_id_opera")
    val operatorId: String,
    @SerializedName("pais_dispositivos")
    val pais_dispositivos: String
)
