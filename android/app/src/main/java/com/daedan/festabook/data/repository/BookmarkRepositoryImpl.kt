package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.OrganizationBookmarkDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val organizationBookmarkDataSource: OrganizationBookmarkDataSource,
) : BookmarkRepository {
    override suspend fun saveOrganizationBookmark(
        organizationId: Long,
        deviceId: Long,
    ): Result<Long> {
        val response =
            organizationBookmarkDataSource
                .saveOrganizationBookmark(
                    organizationId = organizationId,
                    deviceId = deviceId,
                ).toResult()

        return response.mapCatching { it.organizationBookmarkId }
    }

    override suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): Result<Unit> {
        val response = organizationBookmarkDataSource.deleteOrganizationBookmark(organizationBookmarkId)
        val result = response.toResult()

        return result
    }
}
