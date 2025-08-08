package com.daedan.festabook.data.datasource.remote.notice

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.notice.NoticeListResponse
import com.daedan.festabook.data.service.NoticeService

class NoticeDataSourceImpl(
    private val noticeService: NoticeService,
) : NoticeDataSource {
    override suspend fun fetchNotices(): ApiResult<NoticeListResponse> = ApiResult.toApiResult { noticeService.getNotices() }
}
