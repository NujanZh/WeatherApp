package com.nur.weatherapp.data

import android.content.Context
import androidx.core.content.edit

object PreferencesManager {
    private const val PREFS_NAME = "weather_prefs"
    private const val KEY_LAST_CITY = "last_city"

    fun saveLastCity(context: Context, city: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_LAST_CITY, city)
        }
    }

    fun getLastCity(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LAST_CITY, "Prague") ?: "Prague"
    }
}
