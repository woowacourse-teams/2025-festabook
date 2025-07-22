package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.placeDetail.PlaceDetailDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.PlaceDetail
import com.daedan.festabook.domain.repository.PlaceDetailRepository

class PlaceDetailRepositoryImpl(
    private val placeDetailDataSource: PlaceDetailDataSource,
) : PlaceDetailRepository {
    override suspend fun fetchPlaceDetail(): Result<PlaceDetail> {
        val response = placeDetailDataSource.fetchPlaceDetail().toResult()
        return response.map { it.toDomain() }
    }
}
