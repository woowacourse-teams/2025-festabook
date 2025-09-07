package com.daedan.festabook.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRegisterResponse(
    @SerialName("deviceId")
    val id: Long,
)
