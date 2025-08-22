package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.request.FestivalNotificationRequest
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse
import com.daedan.festabook.data.service.FestivalNotificationService

class FestivalNotificationDataSourceImpl(
    private val festivalNotificationService: FestivalNotificationService,
) : FestivalNotificationDataSource {
    override suspend fun saveFestivalNotification(
        festivalId: Long,
        deviceId: Long,
    ): ApiResult<FestivalNotificationResponse> =
        ApiResult.toApiResult {
            festivalNotificationService.saveFestivalNotification(
                festivalId,
                FestivalNotificationRequest(deviceId = deviceId),
            )
        }

    override suspend fun deleteFestivalNotification(festivalNotificationId: Long): ApiResult<Unit> =
        ApiResult.toApiResult {
            festivalNotificationService.deleteFestivalNotification(festivalNotificationId)
        }
}
