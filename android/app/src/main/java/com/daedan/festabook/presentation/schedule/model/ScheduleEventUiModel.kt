package com.daedan.festabook.presentation.schedule.model

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
