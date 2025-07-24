package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.NoticeListResponse

interface NoticeDataSource {
    suspend fun fetchNotices(): ApiResult<NoticeListResponse>
}
