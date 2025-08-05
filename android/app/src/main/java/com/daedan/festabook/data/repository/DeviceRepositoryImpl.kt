package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.device.DeviceDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.DeviceRepository

class DeviceRepositoryImpl(
    private val deviceDataSource: DeviceDataSource,
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
}
