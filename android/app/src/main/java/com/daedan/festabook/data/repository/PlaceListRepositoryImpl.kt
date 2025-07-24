package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.repository.PlaceListRepository

class PlaceListRepositoryImpl(
    private val placeDataSource: PlaceDataSource,
) : PlaceListRepository {
    override suspend fun getPlaces(): Result<List<Place>> {
        val response = placeDataSource.fetchPlaces().toResult()
        return response.map { places -> places.map { it.toDomain() } }
    }
}
