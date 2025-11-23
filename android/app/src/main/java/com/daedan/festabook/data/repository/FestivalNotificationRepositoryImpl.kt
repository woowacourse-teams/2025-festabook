package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.local.DeviceLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalLocalDataSource
import com.daedan.festabook.data.datasource.local.FestivalNotificationLocalDataSource
import com.daedan.festabook.data.datasource.remote.festival.FestivalNotificationDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.FestivalNotificationRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import timber.log.Timber

@ContributesBinding(AppScope::class)
class FestivalNotificationRepositoryImpl @Inject constructor(
    private val festivalNotificationDataSource: FestivalNotificationDataSource,
    private val deviceLocalDataSource: DeviceLocalDataSource,
    private val festivalNotificationLocalDataSource: FestivalNotificationLocalDataSource,
    private val festivalLocalDataSource: FestivalLocalDataSource,
) : FestivalNotificationRepository {
    override suspend fun saveFestivalNotification(): Result<Unit> {
        val deviceId = deviceLocalDataSource.getDeviceId()
        if (deviceId == null) {
            Timber.e("${::FestivalNotificationRepositoryImpl.name}: DeviceId가 없습니다.")
            return Result.failure(IllegalStateException())
        }
        val festivalId = festivalLocalDataSource.getFestivalId()

        val result =
            festivalId?.let {
                festivalNotificationDataSource
                    .saveFestivalNotification(
                        festivalId = it,
                        deviceId = deviceId,
                    ).toResult()
            }
                ?: throw IllegalArgumentException("${::FestivalNotificationRepositoryImpl.javaClass.simpleName}festivalId가 null 입니다.")
        return result
            .mapCatching {
                festivalNotificationLocalDataSource.saveFestivalNotificationId(
                    festivalId,
                    it.festivalNotificationId,
                )
            }
    }

    override suspend fun deleteFestivalNotification(): Result<Unit> {
        val festivalId =
            festivalLocalDataSource.getFestivalId() ?: return Result.failure(
                IllegalStateException(),
            )
        val festivalNotificationId =
            festivalNotificationLocalDataSource.getFestivalNotificationId(festivalId)
        val response =
            festivalNotificationDataSource.deleteFestivalNotification(festivalNotificationId)
        festivalNotificationLocalDataSource.deleteFestivalNotificationId(festivalId)

        return response.toResult()
    }

    override fun getFestivalNotificationIsAllow(): Boolean {
        val festivalId = festivalLocalDataSource.getFestivalId() ?: return false
        return festivalNotificationLocalDataSource.getFestivalNotificationIsAllowed(festivalId)
    }

    override fun setFestivalNotificationIsAllow(isAllowed: Boolean) {
        festivalLocalDataSource.getFestivalId()?.let { festivalId ->
            festivalNotificationLocalDataSource.saveFestivalNotificationIsAllowed(
                festivalId,
                isAllowed,
            )
        }
    }
}
