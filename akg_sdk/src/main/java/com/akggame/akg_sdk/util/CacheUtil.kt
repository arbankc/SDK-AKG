package com.akggame.akg_sdk.util

import android.content.Context
import android.content.SharedPreferences


object CacheUtil {
    val PREFERENCE_KEY = "sdk-akggames"
    lateinit var sharedPref: SharedPreferences

    fun putPreferenceString(key: String, value: String, context: Context) {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value).commit()
    }

    fun putPreferenceInteger(key: String, value: Int, context: Context) {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(key, value).commit()
    }

    fun putPreferenceBoolean(key: String, value: Boolean, context: Context) {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value).commit()
    }

    fun getPreferenceString(key: String, context: Context): String? {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)

        return sharedPref.getString(key, "")
    }

    fun getPreferenceInteger(key: String, context: Context): Int {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        return sharedPref.getInt(key, 0)
    }

    fun getPreferenceBoolean(key: String, context: Context): Boolean {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }

    fun clearPreference(context: Context) {
        sharedPref = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        sharedPref.edit().clear().commit()
    }
}