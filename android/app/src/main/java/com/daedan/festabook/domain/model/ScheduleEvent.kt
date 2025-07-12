package com.daedan.festabook.domain.model

data class ScheduleEvent(
    val id: Long,
    val eventDayId: Long,
    val status: String,
    val startTime: String,
    val endTime: String,
    val title: String,
    val location: String,
)
