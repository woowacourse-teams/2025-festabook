package com.daedan.festabook.logging.model.schedule

import com.daedan.festabook.logging.model.BaseLogData
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleSwipeRefreshLogData(
    override val baseLogData: BaseLogData.CommonLogData,
) : BaseLogData
