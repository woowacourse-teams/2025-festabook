package com.daedan.festabook.data.datasource.remote.lineup

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.lineup.LineupResponse

interface LineupDataSource {
    suspend fun fetchLineup(): ApiResult<List<LineupResponse>>
}
