package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import timber.log.Timber

class FestivalNotificationRepositoryImpl(
    private val festivalNotificationDataSource: FestivalNotificationDataSource,
    private val preferencesManager: AppPreferencesManager,
) : FestivalNotificationRepository {
    override suspend fun saveFestivalNotification(): Result<Unit> {
        val deviceId = preferencesManager.getDeviceId()
        val festivalNotificationId = preferencesManager.getFestivalNotificationId()

        val result =
            festivalNotificationDataSource
                .saveFestivalNotification(
                    festivalNotificationId = festivalNotificationId,
                    deviceId = deviceId,
                ).toResult()

        return result.mapCatching { preferencesManager.saveFestivalNotificationId(it.festivalNotificationId) }
    }

    override suspend fun deleteFestivalNotification(): Result<Unit> {
        val festivalNotificationId = preferencesManager.getFestivalNotificationId()
        val response =
            festivalNotificationDataSource.deleteFestivalNotification(festivalNotificationId)

        preferencesManager.deleteFestivalNotificationId()

        return response.toResult()
    }

    override fun getFestivalNotificationIsAllow(): Boolean = preferencesManager.getFestivalNotificationIsAllowed()

    override fun setFestivalNotificationIsAllow(isAllowed: Boolean) {
        Timber.d("$isAllowed")
        preferencesManager.saveFestivalNotificationIsAllowed(isAllowed)
    }
}
