package com.daedan.festabook.domain.model

data class ScheduleEvent(
    val id: Long,
    val status: ScheduleEventStatus,
    val startTime: String,
    val endTime: String,
    val title: String,
    val location: String,
)
