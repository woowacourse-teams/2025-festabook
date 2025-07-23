package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.OrganizationBookmarkResponse

interface OrganizationBookmarkDataSource {
    suspend fun bookmarkOrganization(
        organizationId: Long,
        deviceId: Long,
    ): ApiResult<OrganizationBookmarkResponse>

    suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): ApiResult<Unit>
}
