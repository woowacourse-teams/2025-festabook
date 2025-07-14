package com.daedan.festabook.presentation.schedule

import android.content.Context
import com.daedan.festabook.R
import com.daedan.festabook.domain.model.ScheduleEventStatus

fun ScheduleEventStatus.toKoreanString(context: Context): String =
    when (this) {
        ScheduleEventStatus.UPCOMING -> context.getString(R.string.schedule_status_upcoming)
        ScheduleEventStatus.ONGOING -> context.getString(R.string.schedule_status_ongoing)
        ScheduleEventStatus.COMPLETED -> context.getString(R.string.schedule_status_completed)
    }

fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
