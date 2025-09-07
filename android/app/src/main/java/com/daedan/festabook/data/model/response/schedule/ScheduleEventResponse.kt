package com.daedan.festabook.data.model.response.schedule

import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleEventResponse(
    @SerialName("eventId")
    val id: Long,
    @SerialName("status")
    val status: ScheduleEventStatus,
    @SerialName("startTime")
    val startTime: String,
    @SerialName("endTime")
    val endTime: String,
    @SerialName("title")
    val title: String,
    @SerialName("location")
    val location: String,
)

fun ScheduleEventResponse.toDomain(): ScheduleEvent =
    ScheduleEvent(
        id = id,
        status = status,
        startTime = startTime,
        endTime = endTime,
        title = title,
        location = location,
    )
