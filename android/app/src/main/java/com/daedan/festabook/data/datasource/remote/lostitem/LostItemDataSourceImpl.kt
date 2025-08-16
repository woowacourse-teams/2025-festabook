package com.daedan.festabook.data.datasource.remote.lostitem

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.lostitem.LostItemResponse
import com.daedan.festabook.data.service.LostItemService

class LostItemDataSourceImpl(
    private val lostItemService: LostItemService,
) : LostItemDataSource {
    override suspend fun fetchAllLostItems(): ApiResult<List<LostItemResponse>> =
        ApiResult.toApiResult { lostItemService.fetchAllLostItems() }
}
