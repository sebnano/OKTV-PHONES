package com.oktv_mobile.ui.model.homemodel

import java.util.Date

data class ChannelXMLModel(
    val id : String,
    val displayName: String,
    var logo: String,
    var programs: ArrayList<ChannelProgramModel>
)

data class ChannelProgramModel(
    val title : String,
    val desc: String,
    val start: Date,
    val stop: Date,
//    val category: ArrayList<String>
)