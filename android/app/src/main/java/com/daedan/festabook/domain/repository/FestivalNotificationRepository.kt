package com.daedan.festabook.domain.repository

interface FestivalNotificationRepository {
    suspend fun saveOrganizationBookmark(
        organizationId: Long,
        deviceId: Long,
    ): Result<Long>

    suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): Result<Unit>
}
