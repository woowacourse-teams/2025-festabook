package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FcmDataSource
import com.daedan.festabook.data.datasource.remote.device.DeviceDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.DeviceRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class DeviceRepositoryImpl @Inject constructor(
    private val deviceDataSource: DeviceDataSource,
    private val deviceLocalDataSource: DeviceLocalDataSource,
    private val fcmDataSource: FcmDataSource,
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
        deviceLocalDataSource.saveDeviceId(deviceId)
    }

    override fun getUuid(): String? = deviceLocalDataSource.getUuid()

    override fun getFcmToken(): String? = fcmDataSource.getFcmToken()

    override fun saveFcmToken(token: String) {
        fcmDataSource.saveFcmToken(token)
    }
}
