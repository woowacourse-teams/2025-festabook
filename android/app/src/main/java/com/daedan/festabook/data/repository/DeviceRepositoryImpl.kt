package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.local.AppDataSource
import com.daedan.festabook.data.datasource.remote.device.DeviceDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.DeviceRepository

class DeviceRepositoryImpl(
    private val deviceDataSource: DeviceDataSource,
    private val appDataSource: AppDataSource,
) : DeviceRepository {
    override suspend fun registerDevice(
        deviceIdentifier: String,
        fcmToken: String,
    ): Result<Long> {
        val response =
            deviceDataSource
                .registerDevice(
                    deviceIdentifier = deviceIdentifier,
                    fcmToken = fcmToken,
                ).toResult()
        return response.mapCatching { it.id }
    }

    override fun saveDeviceId(deviceId: Long) {
        appDataSource.saveDeviceId(deviceId)
    }

    override fun getUuid(): String? = appDataSource.getUuid()

    override fun getFcmToken(): String? = appDataSource.getFcmToken()

    override fun saveFcmToken(token: String) {
        appDataSource.saveFcmToken(token)
    }
}
