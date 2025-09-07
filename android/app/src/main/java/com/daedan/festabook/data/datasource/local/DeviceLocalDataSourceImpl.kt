package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit

class DeviceLocalDataSourceImpl(
    private val prefs: SharedPreferences,
) : DeviceLocalDataSource {
    override fun saveUuid(uuid: String) {
        prefs.edit { putString(KEY_UUID, uuid) }
    }

    override fun getUuid(): String? = prefs.getString(KEY_UUID, null)

    override fun saveDeviceId(deviceId: Long) {
        prefs.edit { putLong(KEY_DEVICE_ID, deviceId) }
    }

    override fun getDeviceId(): Long? {
        val deviceId = prefs.getLong(KEY_DEVICE_ID, DEFAULT_DEVICE_ID)
        return if (deviceId == DEFAULT_DEVICE_ID) null else deviceId
    }

    companion object {
        private const val DEFAULT_DEVICE_ID = -1L
        private const val KEY_DEVICE_ID = "server_device_id"
        private const val KEY_UUID = "device_uuid"
    }
}
