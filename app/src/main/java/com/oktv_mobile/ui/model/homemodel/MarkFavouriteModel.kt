package com.oktv_mobile.ui.model.homemodel

import com.google.gson.annotations.SerializedName

data class MarkFavouriteModel(
    @SerializedName("status")
    val status : ArrayList<StatusData>,
    @SerializedName("nid")
    val nodeId : ArrayList<NodeID>,
    @SerializedName("field_canal")
    val channelId : ArrayList<ChannelId>

)
{
  data class StatusData(
      @SerializedName("value")
      val value : Boolean
  )

    data class NodeID(
        @SerializedName("value")
        val value : Int
    )

    data class ChannelId(
        @SerializedName("target_id")
        val targetId : Int
    )
}
