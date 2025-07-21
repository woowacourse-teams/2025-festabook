package com.daedan.festabook.data.datasource.remote

import com.daedan.festabook.data.model.response.NoticeResponse

interface NoticeDataSource {
    suspend fun fetchNotices(): ApiResult<List<NoticeResponse>>
}
