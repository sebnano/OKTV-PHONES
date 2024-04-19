package com.oktv_mobile.utils

import android.content.Context
import com.oktv_mobile.retrofit.model.UserDetailClass

class MyPreferences {

    companion object {
        fun saveStringInPreference(context: Context, key: String, value: String) {
            val preferences = context.getSharedPreferences("OkTv", Context.MODE_PRIVATE)
            preferences.edit().putString(key, value).apply()
        }

        fun saveIntInPreference(context: Context, key: String, value: Int) {
            val preferences = context.getSharedPreferences("OkTv", Context.MODE_PRIVATE)
            preferences.edit().putInt(key, value).apply()
        }

        fun saveLongPreference(context: Context, key: String, value: Long) {
            val preferences = context.getSharedPreferences("OkTv", Context.MODE_PRIVATE)
            preferences.edit().putLong(key, value).apply()
        }

        fun saveBooleanInPreference(context: Context, key: String, value: Boolean) {
            val preferences = context.getSharedPreferences("OkTv", Context.MODE_PRIVATE)
            preferences.edit().putBoolean(key, value).apply()
        }

        fun getFromPreferences(context: Context, key: String): String {
            return "" + context.getSharedPreferences("OkTv", Context.MODE_PRIVATE).getString(key, "")
        }

        fun getLongFromPreferences(context: Context, key: String): Long {
            return context.getSharedPreferences("OkTv", Context.MODE_PRIVATE).getLong(key, 0)
        }

        fun clearData(context: Context ) {
            UserDetailClass.EMAIL = ""
            UserDetailClass.MOBILE_NUMBER = ""
            UserDetailClass.LANGUAGE_ID = ""
            UserDetailClass.FORGOT_PASS = ""
            UserDetailClass.STATUS = ""
            UserDetailClass.IS_DOB = ""
            UserDetailClass.IS_INTEREST = ""
            UserDetailClass.OTP_FROM = ""
            val preferences = context.getSharedPreferences("OkTv", Context.MODE_PRIVATE)
            preferences.edit().clear().apply()
        }

        fun isUserLoggedIn(context: Context): Boolean {
//            if(Constant.userInfo != null) {
//                return true
//            }
//
//            val data = getFromPreferences(context, Constant.userDataKey) ?: ""
//            if (data.isNotEmpty()) {
//                Constant.userInfo = Gson().fromJson(
//                        getFromPreferences(context, Constant.userDataKey),
//                        UserInfoModel::class.java
//                )
//            }
//            return Constant.userInfo != null
            return true
        }

        fun getFcmToken(context: Context): String {
            return getFromPreferences(context, "fcm") ?: ""
        }

        fun saveFcmToken(context: Context, token: String) {
            saveStringInPreference(context, "fcm", token)
        }
    }
}