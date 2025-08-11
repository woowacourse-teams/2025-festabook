package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.FestivalNotificationRepository

class FestivalNotificationRepositoryImpl(
    private val festivalNotificationDataSource: FestivalNotificationDataSource,
) : FestivalNotificationRepository {
    override suspend fun saveFestivalNotification(
        festivalNotificationId: Long,
        deviceId: Long,
    ): Result<Long> {
        val response =
            festivalNotificationDataSource
                .saveFestivalNotification(
                    festivalNotificationId = festivalNotificationId,
                    deviceId = deviceId,
                ).toResult()

        return response.mapCatching { it.festivalNotificationId }
    }

    override suspend fun deleteFestivalNotification(festivalNotificationId: Long): Result<Unit> {
        val response =
            festivalNotificationDataSource.deleteFestivalNotification(festivalNotificationId)
        val result = response.toResult()

        return result
    }
}
