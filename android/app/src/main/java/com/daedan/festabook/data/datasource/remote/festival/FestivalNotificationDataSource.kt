package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse

interface FestivalNotificationDataSource {
    suspend fun saveFestivalNotification(
        organizationId: Long,
        deviceId: Long,
    ): ApiResult<FestivalNotificationResponse>

    suspend fun deleteFestivalNotification(organizationBookmarkId: Long): ApiResult<Unit>
}
