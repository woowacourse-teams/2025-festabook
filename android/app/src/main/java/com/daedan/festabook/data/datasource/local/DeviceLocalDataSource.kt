package com.daedan.festabook.data.datasource.local

interface DeviceLocalDataSource {
    fun saveUuid(uuid: String)

    fun getUuid(): String?

    fun saveDeviceId(deviceId: Long)

    fun getDeviceId(): Long

    companion object {
        const val DEFAULT_DEVICE_ID = -1L
        const val KEY_UUID = "device_uuid"
        const val KEY_DEVICE_ID = "server_device_id"
    }
}
