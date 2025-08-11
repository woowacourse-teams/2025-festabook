package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.festival.FestivalNotificationResponse

interface OrganizationBookmarkDataSource {
    suspend fun saveOrganizationBookmark(
        organizationId: Long,
        deviceId: Long,
    ): ApiResult<FestivalNotificationResponse>

    suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): ApiResult<Unit>
}
