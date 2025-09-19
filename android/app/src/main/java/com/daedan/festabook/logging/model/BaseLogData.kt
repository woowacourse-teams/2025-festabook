package com.daedan.festabook.logging.model

import com.daedan.festabook.logging.model.LogData
import kotlinx.parcelize.Parcelize

interface BaseLogData : LogData {
    val baseLogData: CommonLogData

    @Parcelize
    data class CommonLogData(
        val festivalId: Long,
        val notificationId: Long,
        val deviceInfo: String,
        val eventTime: String,
        val userId: String,
        val sessionId: Long,
    ) : LogData
}
