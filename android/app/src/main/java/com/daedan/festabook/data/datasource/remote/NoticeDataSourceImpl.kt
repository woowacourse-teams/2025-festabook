package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.NoticeResponse
import com.daedan.festabook.data.service.NoticeService

class NoticeDataSourceImpl(
    private val noticeService: NoticeService,
) : NoticeDataSource {
    override suspend fun fetchNotices(): ApiResult<List<NoticeResponse>> = ApiResult.toApiResult { noticeService.getNotices() }
}
