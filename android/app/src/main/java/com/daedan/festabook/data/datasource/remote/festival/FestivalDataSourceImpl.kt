package com.daedan.festabook.data.datasource.remote.festival

import com.daedan.festabook.data.datasource.remote.ApiResult
import com.daedan.festabook.data.model.response.UniversityResponse
import com.daedan.festabook.data.model.response.festival.FestivalResponse
import com.daedan.festabook.data.service.FestivalService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class FestivalDataSourceImpl @Inject constructor(
    private val festivalService: FestivalService,
) : FestivalDataSource {
    override suspend fun fetchFestival(): ApiResult<FestivalResponse> =
        ApiResult.toApiResult {
            festivalService.fetchOrganization()
        }

    override suspend fun findUniversitiesByName(universityName: String): ApiResult<List<UniversityResponse>> =
        ApiResult.toApiResult {
            festivalService.findUniversitiesByName(
                universityName = universityName,
            )
        }
}
