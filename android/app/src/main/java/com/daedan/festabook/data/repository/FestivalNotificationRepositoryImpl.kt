package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.FestivalNotificationRepository

class FestivalNotificationRepositoryImpl(
    private val festivalNotificationDataSource: FestivalNotificationDataSource,
) : FestivalNotificationRepository {
    override suspend fun saveOrganizationBookmark(
        organizationId: Long,
        deviceId: Long,
    ): Result<Long> {
        val response =
            festivalNotificationDataSource
                .saveOrganizationBookmark(
                    organizationId = organizationId,
                    deviceId = deviceId,
                ).toResult()

        return response.mapCatching { it.festivalNotificationId }
    }

    override suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): Result<Unit> {
        val response =
            festivalNotificationDataSource.deleteOrganizationBookmark(organizationBookmarkId)
        val result = response.toResult()

        return result
    }
}
