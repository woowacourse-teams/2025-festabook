package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.request.FestivalNotificationRequest
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse
import com.daedan.festabook.data.service.OrganizationBookmarkService

class FestivalNotificationDataSourceImpl(
    private val organizationBookmarkService: OrganizationBookmarkService,
) : FestivalNotificationDataSource {
    override suspend fun saveFestivalNotification(
        organizationId: Long,
        deviceId: Long,
    ): ApiResult<FestivalNotificationResponse> =
        ApiResult.toApiResult {
            organizationBookmarkService.bookmarkOrganization(
                organizationId,
                FestivalNotificationRequest(deviceId = deviceId),
            )
        }

    override suspend fun deleteFestivalNotification(organizationBookmarkId: Long): ApiResult<Unit> =
        ApiResult.toApiResult {
            organizationBookmarkService.deleteOrganizationBookmark(organizationBookmarkId)
        }
}
