package com.daedan.festabook.domain.repository

interface FestivalNotificationRepository {
    suspend fun saveFestivalNotification(
        organizationId: Long,
        deviceId: Long,
    ): Result<Long>

    suspend fun deleteFestivalNotification(organizationBookmarkId: Long): Result<Unit>
}
