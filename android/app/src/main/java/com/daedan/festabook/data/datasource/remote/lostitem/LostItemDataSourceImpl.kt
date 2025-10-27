package com.daedan.festabook.data.datasource.remote.lostitem

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.lostitem.LostGuideItemResponse
import com.daedan.festabook.data.model.response.lostitem.LostItemResponse
import com.daedan.festabook.data.service.FestivalService
import com.daedan.festabook.data.service.LostItemService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class LostItemDataSourceImpl @Inject constructor(
    private val lostItemService: LostItemService,
    private val festivalService: FestivalService,
) : LostItemDataSource {
    override suspend fun fetchAllLostItems(): ApiResult<List<LostItemResponse>> =
        ApiResult.toApiResult { lostItemService.fetchAllLostItems() }

    override suspend fun fetchLostGuideItem(): ApiResult<LostGuideItemResponse> =
        ApiResult.toApiResult { festivalService.fetchLostGuideItem() }
}
