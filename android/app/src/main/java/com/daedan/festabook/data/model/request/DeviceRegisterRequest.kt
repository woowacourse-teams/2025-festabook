package com.daedan.festabook.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRegisterRequest(
    @SerialName("deviceIdentifier")
    val deviceIdentifier: String,
    @SerialName("fcmToken")
    val fcmToken: String,
)
