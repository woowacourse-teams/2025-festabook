package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.request.FestivalNotificationRequest
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse
import com.daedan.festabook.data.service.OrganizationBookmarkService

class OrganizationBookmarkDataSourceImpl(
    private val organizationBookmarkService: OrganizationBookmarkService,
) : OrganizationBookmarkDataSource {
    override suspend fun saveOrganizationBookmark(
        organizationId: Long,
        deviceId: Long,
    ): ApiResult<FestivalNotificationResponse> =
        ApiResult.toApiResult {
            organizationBookmarkService.bookmarkOrganization(
                organizationId,
                FestivalNotificationRequest(deviceId = deviceId),
            )
        }

    override suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): ApiResult<Unit> =
        ApiResult.toApiResult {
            organizationBookmarkService.deleteOrganizationBookmark(organizationBookmarkId)
        }
}
