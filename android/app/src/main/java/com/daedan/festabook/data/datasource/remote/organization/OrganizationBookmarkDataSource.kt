package com.daedan.festabook.data.datasource.remote.organization

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.organization.OrganizationBookmarkResponse

interface OrganizationBookmarkDataSource {
    suspend fun saveOrganizationBookmark(
        organizationId: Long,
        deviceId: Long,
    ): ApiResult<OrganizationBookmarkResponse>

    suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): ApiResult<Unit>
}
