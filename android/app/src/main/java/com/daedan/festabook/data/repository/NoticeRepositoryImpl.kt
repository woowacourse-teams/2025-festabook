package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.NoticeDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.domain.model.Notice
import com.daedan.festabook.domain.repository.NoticeRepository

class NoticeRepositoryImpl(
    private val noticeDataSource: NoticeDataSource,
) : NoticeRepository {
    override suspend fun getNotice(): Result<List<Notice>> =
        runCatching {
            noticeDataSource.getNotices().getOrThrow().map { it.toDomain() }
        }
}
