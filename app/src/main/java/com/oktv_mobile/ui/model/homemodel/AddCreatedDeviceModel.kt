package com.oktv_mobile.ui.model.homemodel

import com.google.gson.annotations.SerializedName

data class AddCreatedDeviceModel (
    @SerializedName("nid")
    val nid: ArrayList<NidData>
 ) {
    data class NidData(
        @SerializedName("value")
        val value: Int
    )
}