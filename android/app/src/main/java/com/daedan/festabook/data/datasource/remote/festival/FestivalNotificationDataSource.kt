package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse

interface FestivalNotificationDataSource {
    suspend fun saveFestivalNotification(
        festivalId: Long,
        deviceId: Long,
    ): ApiResult<FestivalNotificationResponse>

    suspend fun deleteFestivalNotification(festivalNotificationId: Long): ApiResult<Unit>
}
