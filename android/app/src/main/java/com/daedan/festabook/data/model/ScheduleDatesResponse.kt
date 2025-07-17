package com.daedan.festabook.data.model

import com.daedan.festabook.domain.model.ScheduleDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// @Serializable
// data class ScheduleDatesResponse(
//    val scheduleDates: List<ScheduleDate>,
// ) {
@Serializable
data class ScheduleDateResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("date")
    val date: String,
)
// }

fun ScheduleDateResponse.toDomain(): ScheduleDate =
    ScheduleDate(
        id = id,
        date = date,
    )
