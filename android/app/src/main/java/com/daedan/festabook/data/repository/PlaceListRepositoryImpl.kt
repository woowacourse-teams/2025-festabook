package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.placeList.PlaceListDataSource
import com.daedan.festabook.data.datasource.remote.placeMap.PlaceMapDataSource
import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceGeography
import com.daedan.festabook.domain.repository.PlaceListRepository

class PlaceListRepositoryImpl(
    private val placeDataSource: PlaceDataSource,
    private val placeMapDataSource: PlaceMapDataSource,
) : PlaceListRepository {
    override suspend fun getPlaces(): Result<List<Place>> {
        val response = placeDataSource.fetchPlaces().toResult()
        return response.map { places -> places.map { it.toDomain() } }
    }

    override suspend fun fetchPlaceGeography(): Result<PlaceGeography> {
        val response = placeMapDataSource.fetchPlacesGeography().toResult()
        return response.map { it.toDomain() }
    }
}
