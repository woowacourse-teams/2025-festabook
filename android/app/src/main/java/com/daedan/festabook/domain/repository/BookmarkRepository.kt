package com.daedan.festabook.domain.repository

interface BookmarkRepository {
    suspend fun bookmarkOrganization(
        organizationId: Long,
        deviceId: Long,
    ): Result<Long>

    suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): Result<Unit>
}
