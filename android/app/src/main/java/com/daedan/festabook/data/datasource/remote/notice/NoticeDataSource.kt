package com.daedan.festabook.data.datasource.remote.notice

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.notice.NoticeListResponse

interface NoticeDataSource {
    suspend fun fetchNotices(): ApiResult<NoticeListResponse>
}
