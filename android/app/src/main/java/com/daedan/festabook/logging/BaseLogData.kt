package com.daedan.festabook.logging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaseLogData(
    val festivalId: Long,
    val notificationId: Long,
    val deviceInfo: String,
    val eventTime: String,
    val userId: String,
    val sessionId: Long,
) : Parcelable
