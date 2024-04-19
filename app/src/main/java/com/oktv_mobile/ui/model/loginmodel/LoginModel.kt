package com.oktv_mobile.ui.model.loginmodel

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("current_user")
    val currentUser: CurrentUser,
    @SerializedName("csrf_token")
    val csrfToken: String,
    @SerializedName("logout_token")
    val logoutToken: String,
    val message: String
 )
{
    data class CurrentUser (
        @SerializedName("uid")
        val uId: String,
        @SerializedName("name")
        val name: String
    )
}