package com.daedan.festabook.presentation.schedule.model

import com.daedan.festabook.domain.model.ScheduleDate

data class ScheduleDateUiModel(
    val id: Long,
    val date: String,
)

fun ScheduleDate.toUiModel(): ScheduleDateUiModel =
    ScheduleDateUiModel(
        id = id,
        date = date,
    )
