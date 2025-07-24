package com.daedan.festabook.domain.repository

interface DeviceRepository {
    suspend fun registerDevice(
        deviceIdentifier: String,
        fcmToken: String,
    ): Result<Long>
}
