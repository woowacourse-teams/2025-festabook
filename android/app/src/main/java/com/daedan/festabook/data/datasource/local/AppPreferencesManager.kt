package com.daedan.festabook.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreferencesManager(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUuid(uuid: String) {
        prefs.edit { putString(KEY_UUID, uuid) }
    }

    fun getUuid(): String? = prefs.getString(KEY_UUID, null)

    fun saveFcmToken(token: String) {
        prefs.edit { putString(KEY_FCM_TOKEN, token) }
    }

    fun getFcmToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    fun saveDeviceId(deviceId: Long) {
        prefs.edit { putLong(KEY_DEVICE_ID, deviceId) }
    }

    fun getDeviceId(): Long = prefs.getLong(KEY_DEVICE_ID, DEFAULT_DEVICE_ID)

    fun saveFestivalNotificationId(festivalNotificationId: Long) {
        prefs.edit { putLong(KEY_FESTIVAL_NOTIFICATION_ID, festivalNotificationId) }
    }

    fun getFestivalNotificationId(): Long = prefs.getLong(KEY_FESTIVAL_NOTIFICATION_ID, DEFAULT_FESTIVAL_NOTIFICATION_ID)

    fun deleteFestivalNotificationId() {
        prefs.edit { remove(KEY_FESTIVAL_NOTIFICATION_ID) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }

    fun saveFestivalNotificationIsAllowed(isAllowed: Boolean) {
        prefs.edit { putBoolean(KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED, isAllowed) }
    }

    fun getFestivalNotificationIsAllowed(): Boolean = prefs.getBoolean(KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED, false)

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_UUID = "device_uuid"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_DEVICE_ID = "server_device_id"
        private const val KEY_FESTIVAL_NOTIFICATION_ID = "festival_notification_id"
        private const val DEFAULT_FESTIVAL_NOTIFICATION_ID = -1L
        private const val KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED = "key_festival_notification_allowed"
        const val DEFAULT_DEVICE_ID = -1L
    }
}
