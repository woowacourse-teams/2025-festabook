package com.daedan.festabook.data.datasource.remote.lineup

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.lineup.LineupResponse
import com.daedan.festabook.data.service.FestivalLineupService

class LineupDataSourceImpl(
    private val festivalLineupService: FestivalLineupService,
) : LineupDataSource {
    override suspend fun fetchLineup(): ApiResult<List<LineupResponse>> =
        ApiResult.toApiResult {
            festivalLineupService.fetchLineup()
        }
}
