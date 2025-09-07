package com.daedan.festabook.data.service

import com.daedan.festabook.data.model.response.lostitem.LostItemResponse
import retrofit2.Response
import retrofit2.http.GET

interface LostItemService {
    @GET("lost-items")
    suspend fun fetchAllLostItems(): Response<List<LostItemResponse>>
}
