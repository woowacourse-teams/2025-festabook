package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.model.response.place.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.PlaceDetail
import com.daedan.festabook.domain.repository.PlaceDetailRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
class PlaceDetailRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource,
) : PlaceDetailRepository {
    override suspend fun getPlaceDetail(placeId: Long): Result<PlaceDetail> {
        val response = placeDataSource.fetchPlaceDetail(placeId).toResult()
        return response.mapCatching { it.toDomain() }
    }
}
