package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.OrganizationBookmarkDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val organizationBookmarkDataSource: OrganizationBookmarkDataSource,
) : BookmarkRepository {
    override suspend fun bookmarkOrganization(
        organizationId: Long,
        deviceId: Long,
    ): Result<Long> {
        val response =
            organizationBookmarkDataSource
                .bookmarkOrganization(
                    organizationId = organizationId,
                    deviceId = deviceId,
                ).toResult()

        return response.map { it.organizationBookmarkId }
    }

    override suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): Result<Unit> {
        val response = organizationBookmarkDataSource.deleteOrganizationBookmark(organizationBookmarkId)
        val result = response.toResult()

        return result
    }
}
