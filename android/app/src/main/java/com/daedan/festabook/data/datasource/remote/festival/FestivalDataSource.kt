package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.UniversityResponse
import com.daedan.festabook.data.model.response.festival.FestivalResponse

interface FestivalDataSource {
    suspend fun fetchFestival(): ApiResult<FestivalResponse>

    suspend fun findUniversitiesByName(universityName: String): ApiResult<List<UniversityResponse>>
}
