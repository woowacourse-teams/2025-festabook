package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.request.DeviceRegisterRequest
import com.daedan.festabook.data.model.response.DeviceRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceService {
    @POST("devices")
    suspend fun registerDevice(
        @Body deviceRegisterRequest: DeviceRegisterRequest,
    ): Response<DeviceRegisterResponse>
}
