package com.daedan.festabook.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppDataSourceImpl(
    context: Context,
) : AppDataSource {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveUuid(uuid: String) {
        prefs.edit { putString(KEY_UUID, uuid) }
    }

    override fun getUuid(): String? = prefs.getString(KEY_UUID, null)

    override fun saveFcmToken(token: String) {
        prefs.edit { putString(KEY_FCM_TOKEN, token) }
    }

    override fun getFcmToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    override fun saveDeviceId(deviceId: Long) {
        prefs.edit { putLong(KEY_DEVICE_ID, deviceId) }
    }

    override fun getDeviceId(): Long = prefs.getLong(KEY_DEVICE_ID, DEFAULT_DEVICE_ID)

    override fun saveFestivalNotificationId(festivalNotificationId: Long) {
        prefs.edit { putLong(KEY_FESTIVAL_NOTIFICATION_ID, festivalNotificationId) }
    }

    override fun getFestivalNotificationId(): Long = prefs.getLong(KEY_FESTIVAL_NOTIFICATION_ID, DEFAULT_FESTIVAL_NOTIFICATION_ID)

    override fun deleteFestivalNotificationId() {
        prefs.edit { remove(KEY_FESTIVAL_NOTIFICATION_ID) }
    }

    override fun clearAll() {
        prefs.edit { clear() }
    }

    override fun saveFestivalNotificationIsAllowed(isAllowed: Boolean) {
        prefs.edit { putBoolean(KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED, isAllowed) }
    }

    override fun getFestivalNotificationIsAllowed(): Boolean = prefs.getBoolean(KEY_FESTIVAL_NOTIFICATION_IS_ALLOWED, false)

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
