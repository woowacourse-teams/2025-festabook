package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.DeviceRegisterResponse

interface DeviceDataSource {
    suspend fun registerDevice(
        deviceIdentifier: String,
        fcmToken: String,
    ): ApiResult<DeviceRegisterResponse>
}
