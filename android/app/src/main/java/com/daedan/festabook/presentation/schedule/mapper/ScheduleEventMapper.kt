package com.daedan.festabook.presentation.schedule.mapper

import android.content.Context
import com.daedan.festabook.R
import com.daedan.festabook.domain.model.ScheduleEvent
import com.daedan.festabook.domain.model.ScheduleEventStatus
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiModel
import com.daedan.festabook.presentation.schedule.model.ScheduleEventUiStatus

fun ScheduleEventUiStatus.toKoreanString(context: Context): String =
    when (this) {
        ScheduleEventUiStatus.UPCOMING -> context.getString(R.string.schedule_status_upcoming)
        ScheduleEventUiStatus.ONGOING -> context.getString(R.string.schedule_status_ongoing)
        ScheduleEventUiStatus.COMPLETED -> context.getString(R.string.schedule_status_completed)
    }

fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

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
