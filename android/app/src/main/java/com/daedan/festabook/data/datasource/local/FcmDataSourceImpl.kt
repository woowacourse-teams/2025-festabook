package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class FcmDataSourceImpl @Inject constructor(
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
