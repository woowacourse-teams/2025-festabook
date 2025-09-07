package com.daedan.festabook.data.model.response.schedule

import com.daedan.festabook.domain.model.ScheduleDate
import com.daedan.festabook.domain.model.toLocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDateResponse(
    @SerialName("eventDateId")
    val id: Long,
    @SerialName("date")
    val date: String,
)

fun ScheduleDateResponse.toDomain(): ScheduleDate =
    ScheduleDate(
        id = id,
        date = date.toLocalDate(),
    )
