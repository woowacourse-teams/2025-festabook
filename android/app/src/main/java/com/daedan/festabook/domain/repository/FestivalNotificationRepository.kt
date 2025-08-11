package com.daedan.festabook.domain.repository

interface FestivalNotificationRepository {
    suspend fun saveFestivalNotification(
        festivalNotificationId: Long,
        deviceId: Long,
    ): Result<Long>

    suspend fun deleteFestivalNotification(festivalNotificationId: Long): Result<Unit>
}
