package com.daedan.festabook.domain.repository

interface DeviceRepository {
    suspend fun registerDevice(
        deviceIdentifier: String,
        fcmToken: String,
    ): Result<Long>

    fun saveDeviceId(deviceId: Long)

    fun getUuid(): String?

    fun getFcmToken(): String?

    fun saveFcmToken(token: String)
}
