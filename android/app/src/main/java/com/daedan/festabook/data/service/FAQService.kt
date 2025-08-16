package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.faq.FAQResponse
import retrofit2.Response
import retrofit2.http.GET

interface FAQService {
    @GET("questions")
    suspend fun fetchAllFAQs(): Response<List<FAQResponse>>
}
