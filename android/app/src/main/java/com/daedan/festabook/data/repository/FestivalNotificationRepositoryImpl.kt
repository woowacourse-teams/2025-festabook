package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.local.AppDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import timber.log.Timber

class FestivalNotificationRepositoryImpl(
    private val festivalNotificationDataSource: FestivalNotificationDataSource,
    private val appDataSource: AppDataSource,
) : FestivalNotificationRepository {
    override suspend fun saveFestivalNotification(): Result<Unit> {
        val deviceId = appDataSource.getDeviceId()
        val festivalNotificationId = appDataSource.getFestivalNotificationId()

        val result =
            festivalNotificationDataSource
                .saveFestivalNotification(
                    festivalNotificationId = festivalNotificationId,
                    deviceId = deviceId,
                ).toResult()

        return result.mapCatching { appDataSource.saveFestivalNotificationId(it.festivalNotificationId) }
    }

    override suspend fun deleteFestivalNotification(): Result<Unit> {
        val festivalNotificationId = appDataSource.getFestivalNotificationId()
        val response =
            festivalNotificationDataSource.deleteFestivalNotification(festivalNotificationId)

        appDataSource.deleteFestivalNotificationId()

        return response.toResult()
    }

    override fun getFestivalNotificationIsAllow(): Boolean = appDataSource.getFestivalNotificationIsAllowed()

    override fun setFestivalNotificationIsAllow(isAllowed: Boolean) {
        Timber.d("$isAllowed")
        appDataSource.saveFestivalNotificationIsAllowed(isAllowed)
    }
}
