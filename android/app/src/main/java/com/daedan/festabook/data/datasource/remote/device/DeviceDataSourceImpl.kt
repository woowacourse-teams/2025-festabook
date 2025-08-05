package com.daedan.festabook.data.datasource.remote.device

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.request.DeviceRegisterRequest
import com.daedan.festabook.data.model.response.DeviceRegisterResponse
import com.daedan.festabook.data.service.DeviceService

class DeviceDataSourceImpl(
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
