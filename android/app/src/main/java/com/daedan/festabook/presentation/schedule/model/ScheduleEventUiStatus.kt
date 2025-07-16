package com.daedan.festabook.presentation.schedule.model

import android.content.Context
import com.daedan.festabook.R

enum class ScheduleEventUiStatus {
    UPCOMING,
    ONGOING,
    COMPLETED,
}

fun ScheduleEventUiStatus.toKoreanString(context: Context): String =
    when (this) {
        ScheduleEventUiStatus.UPCOMING -> context.getString(R.string.schedule_status_upcoming)
        ScheduleEventUiStatus.ONGOING -> context.getString(R.string.schedule_status_ongoing)
        ScheduleEventUiStatus.COMPLETED -> context.getString(R.string.schedule_status_completed)
    }
