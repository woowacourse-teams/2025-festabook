package com.daedan.festabook.logging.model.schedule

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleEventClickLogData(
    override val baseLogData: BaseLogData.CommonLogData,
    val eventId: Long,
    val eventTitle: String,
) : BaseLogData
