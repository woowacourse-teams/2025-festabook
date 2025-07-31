package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.NoticeDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Notice
import com.daedan.festabook.domain.repository.NoticeRepository

class NoticeRepositoryImpl(
    private val noticeDataSource: NoticeDataSource,
) : NoticeRepository {
    override suspend fun fetchNotices(): Result<List<Notice>> {
        val response = noticeDataSource.fetchNotices().toResult()
        return response.mapCatching { noticeListResponse ->
            val pinned = noticeListResponse.pinned.map { it.toDomain() }
            val unpinned = noticeListResponse.unpinned.map { it.toDomain() }
            pinned + unpinned
        }
    }
}
