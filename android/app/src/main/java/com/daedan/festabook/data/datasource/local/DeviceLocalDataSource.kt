package com.daedan.festabook.data.datasource.local

interface DeviceLocalDataSource {
    fun saveUuid(uuid: String)

    fun getUuid(): String?

    fun saveDeviceId(deviceId: Long)

    fun getDeviceId(): Long?
}
