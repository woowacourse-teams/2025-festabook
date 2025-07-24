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

    fun saveBookmarkId(bookmarkId: Long) {
        prefs.edit { putLong(KEY_BOOKMARK_ID, bookmarkId) }
    }

    fun getBookmarkId(): Long? {
        val id = prefs.getLong(KEY_BOOKMARK_ID, DEFAULT_BOOKMARK_ID)
        return if (id == DEFAULT_BOOKMARK_ID) null else id
    }

    fun clearBookmarkId() {
        prefs.edit { remove(KEY_BOOKMARK_ID) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_UUID = "device_uuid"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_DEVICE_ID = "server_device_id"
        private const val KEY_BOOKMARK_ID = "bookmark_id"
        private const val DEFAULT_BOOKMARK_ID = -1L
        const val DEFAULT_DEVICE_ID = -1L
    }
}
