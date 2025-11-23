package com.daedan.festabook.data.datasource.remote.notice

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.notice.NoticeListResponse
import com.daedan.festabook.data.service.NoticeService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class NoticeDataSourceImpl @Inject constructor(
    private val noticeService: NoticeService,
) : NoticeDataSource {
    override suspend fun fetchNotices(): ApiResult<NoticeListResponse> = ApiResult.toApiResult { noticeService.getNotices() }
}
