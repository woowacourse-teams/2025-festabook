package com.daedan.festabook.domain.model

data class ScheduleEvent(
    val id: Long,
    val eventDayId: Long,
    val status: ScheduleEventStatus,
    val startTime: String,
    val endTime: String,
    val title: String,
    val description: String,
    val location: String,
    val isBookmarked: Boolean = true,
)
