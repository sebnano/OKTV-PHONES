package com.oktv_mobile.utils

import android.content.Context
import android.content.SharedPreferences
import com.oktv_mobile.ui.model.loginmodel.LanguageStringModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel
import com.oktv_mobile.ui.model.homemodel.ChannelProgramModel
import com.oktv_mobile.ui.model.homemodel.ChannelXMLModel

object SharedPreferencesHelper {

    private const val PREFS_NAME = "MyPrefs"
    private const val KEY_ARRAY_LIST = "myArrayList"

    fun saveArrayList(context: Context, list: ArrayList<LanguageStringModel>) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(KEY_ARRAY_LIST, json)
        editor.apply()
    }

    fun getArrayList(context: Context): ArrayList<LanguageStringModel>? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_ARRAY_LIST, null)
        val type = object : TypeToken<ArrayList<LanguageStringModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun removeArrayList(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val list = ArrayList<LanguageStringModel>()
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(KEY_ARRAY_LIST, json)
        editor.apply()
    }

    fun saveVideoList(context: Context, list: ArrayList<ChannelDataModel>) {
        val prefs: SharedPreferences = context.getSharedPreferences("oktv", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(KEY_ARRAY_LIST, json)
        editor.apply()
    }

    fun getVideoList(context: Context): ArrayList<ChannelDataModel>? {
        val prefs: SharedPreferences = context.getSharedPreferences("oktv", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_ARRAY_LIST, null)
        val type = object : TypeToken<ArrayList<ChannelDataModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveProgramList(context: Context, list: ArrayList<ChannelProgramModel>) {
        val prefs: SharedPreferences = context.getSharedPreferences("GetApp", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(KEY_ARRAY_LIST, json)
        editor.apply()
    }

    fun getProgramList(context: Context): ArrayList<ChannelProgramModel>? {
        val prefs: SharedPreferences = context.getSharedPreferences("GetApp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_ARRAY_LIST, null)
        val type = object : TypeToken<ArrayList<ChannelProgramModel>>() {}.type
        return gson.fromJson(json, type)
    }
}