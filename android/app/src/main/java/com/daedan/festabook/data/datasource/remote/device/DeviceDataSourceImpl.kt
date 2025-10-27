package com.daedan.festabook.data.datasource.remote.device

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.request.DeviceRegisterRequest
import com.daedan.festabook.data.model.response.DeviceRegisterResponse
import com.daedan.festabook.data.service.DeviceService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class DeviceDataSourceImpl @Inject constructor(
    private val deviceService: DeviceService,
) : DeviceDataSource {
    override suspend fun registerDevice(
        deviceIdentifier: String,
        fcmToken: String,
    ): ApiResult<DeviceRegisterResponse> =
        ApiResult.toApiResult {
            deviceService.registerDevice(
                DeviceRegisterRequest(
                    deviceIdentifier = deviceIdentifier,
                    fcmToken = fcmToken,
                ),
            )
        }
}
