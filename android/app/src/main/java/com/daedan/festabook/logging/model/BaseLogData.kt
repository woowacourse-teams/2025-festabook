package com.daedan.festabook.logging.model

import com.daedan.festabook.logging.model.LogData

interface BaseLogData : LogData {
    val baseLogData: CommonLogData
}
