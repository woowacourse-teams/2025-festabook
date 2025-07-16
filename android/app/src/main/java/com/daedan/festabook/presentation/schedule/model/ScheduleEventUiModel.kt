package com.daedan.festabook.presentation.schedule.model

import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus

data class ScheduleEventUiModel(
    val id: Long,
    val eventDayId: Long,
    val status: ScheduleEventUiStatus,
    val startTime: String,
    val endTime: String,
    val title: String,
    val location: String,
    val isBookmarked: Boolean = false,
)

fun ScheduleEvent.toUiModel(): ScheduleEventUiModel =
    ScheduleEventUiModel(
        id = id,
        eventDayId = eventDayId,
        status =
            when (status) {
                ScheduleEventStatus.COMPLETED -> ScheduleEventUiStatus.COMPLETED
                ScheduleEventStatus.ONGOING -> ScheduleEventUiStatus.ONGOING
                ScheduleEventStatus.UPCOMING -> ScheduleEventUiStatus.UPCOMING
            },
        startTime = startTime,
        endTime = endTime,
        title = title,
        location = location,
    )
