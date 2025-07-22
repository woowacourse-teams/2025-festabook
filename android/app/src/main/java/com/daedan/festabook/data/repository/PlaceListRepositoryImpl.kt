package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.placeList.PlaceListDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.repository.PlaceListRepository

class PlaceListRepositoryImpl(
    private val placeListDataSource: PlaceListDataSource,
) : PlaceListRepository {
    override suspend fun fetchPlaces(): Result<List<Place>> {
        val response = placeListDataSource.fetchPlaces().toResult()
        return response.map { places -> places.map { it.toDomain() } }
    }
}
