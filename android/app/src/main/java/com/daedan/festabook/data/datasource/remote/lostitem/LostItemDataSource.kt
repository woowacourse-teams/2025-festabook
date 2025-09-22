package com.daedan.festabook.data.datasource.remote.lostitem

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.lostitem.LostGuideItemResponse
import com.daedan.festabook.data.model.response.lostitem.LostItemResponse

interface LostItemDataSource {
    suspend fun fetchAllLostItems(): ApiResult<List<LostItemResponse>>

    suspend fun fetchLostGuideItem(): ApiResult<LostGuideItemResponse>
}
