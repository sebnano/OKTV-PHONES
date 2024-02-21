package com.app.oktv.utils

import android.content.Context
import android.preference.PreferenceManager
import java.util.Locale

object Localization {
    private const val LANGUAGE = "language_pref"

    fun setLanguage(context: Context,languageCode : String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(LANGUAGE,languageCode).apply()
        updateLanguage(context,languageCode)
    }

    private fun updateLanguage(context: Context,languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration,resources.displayMetrics)
    }
}