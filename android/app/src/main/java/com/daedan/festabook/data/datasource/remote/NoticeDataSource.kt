package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.NoticeResponse
import com.daedan.festabook.data.service.NoticeService
import com.daedan.festabook.data.util.safeApiCall

interface NoticeDataSource {
    suspend fun getNotices(): Result<List<NoticeResponse>>
}

class NoticeDataSourceImpl(
    private val noticeService: NoticeService,
) : NoticeDataSource {
    override suspend fun getNotices(): Result<List<NoticeResponse>> =
        safeApiCall {
            noticeService.getNotices()
        }
}
