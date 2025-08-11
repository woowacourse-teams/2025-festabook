package com.daedan.festabook.data.model.response.festival

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FestivalNotificationResponse(
    @SerialName("festivalNotificationId")
    val festivalNotificationId: Long,
)
