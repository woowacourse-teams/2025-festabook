package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.notice.NoticeListResponse
import retrofit2.Response
import retrofit2.http.GET

interface NoticeService {
    @GET("announcements")
    suspend fun getNotices(): Response<NoticeListResponse>
}
