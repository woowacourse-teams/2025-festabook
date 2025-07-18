package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.NoticeResponse
import retrofit2.Response
import retrofit2.http.GET

interface NoticeService {
    @GET("/announcement")
    suspend fun getNotices(): Response<List<NoticeResponse>>
}
