package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.OrganizationBookmarkDataSource
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.repository.BookmarkRepository
import timber.log.Timber

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

        Timber.d("📦 Raw Response: $response")
        Timber.d("📤 요청 값: organizationId=$organizationId, deviceId=$deviceId")

        return response.map { it.organizationBookmarkId }
    }

    override suspend fun deleteOrganizationBookmark(organizationBookmarkId: Long): Result<Unit> {
        val response = organizationBookmarkDataSource.deleteOrganizationBookmark(organizationBookmarkId)

        // 👇 로그 찍기
        Timber.d("📤 요청 bookmarkId = $organizationBookmarkId")
        Timber.d("📦 Raw Response from API = $response")

        val result = response.toResult()

        // 👇 실패했을 경우 상세 로그
        if (result.isFailure) {
            Timber.e("❌ 북마크 삭제 실패: ${(result.exceptionOrNull()?.message)}")
        }

        return result
    }
}
