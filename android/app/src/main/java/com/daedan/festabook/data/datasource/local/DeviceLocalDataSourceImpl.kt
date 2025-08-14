package com.daedan.festabook.data.datasource.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource.Companion.DEFAULT_DEVICE_ID
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource.Companion.KEY_DEVICE_ID
import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource.Companion.KEY_UUID

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

    override fun getDeviceId(): Long = prefs.getLong(KEY_DEVICE_ID, DEFAULT_DEVICE_ID)
}
