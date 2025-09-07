package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit

class FcmDataSourceImpl(
    private val prefs: SharedPreferences,
) : FcmDataSource {
    override fun saveFcmToken(token: String) {
        prefs.edit { putString(KEY_FCM_TOKEN, token) }
    }

    override fun getFcmToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    companion object {
        private const val KEY_FCM_TOKEN = "fcm_token"
    }
}
