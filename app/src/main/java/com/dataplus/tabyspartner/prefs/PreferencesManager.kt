package com.dataplus.tabyspartner.prefs

import android.content.Context

object PreferencesManager {
    private const val PREF_USER_PHONE_NUMBER = "user_phone_number"
    private const val PREF_USER_REGISTERED = false


    fun saveUserPhoneNumber(context: Context, userPhoneNumber: String) {
        getPrefs(context).edit().putString(PREF_USER_PHONE_NUMBER, userPhoneNumber).apply()
    }

    fun getUserPhoneNumber(context: Context) =
            getPrefs(context).getString(PREF_USER_PHONE_NUMBER, "")

    fun getPrefs(context: Context) = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
}