package com.daedan.festabook.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FestivalNotificationRequest(
    @SerialName("deviceId")
    val deviceId: Long,
)
