package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.model.response.toDomain
import com.daedan.festabook.data.util.toResult
import com.daedan.festabook.domain.model.OrganizationGeography
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceGeography
import com.daedan.festabook.domain.repository.PlaceListRepository

class PlaceListRepositoryImpl(
    private val placeDataSource: PlaceDataSource,
) : PlaceListRepository {
    override suspend fun getPlaces(): Result<List<Place>> {
        val response = placeDataSource.fetchPlaces().toResult()
        return response.map { places -> places.map { it.toDomain() } }
    }

    override suspend fun getOrganizationGeography(): Result<OrganizationGeography> {
        val response = placeDataSource.fetchOrganizationGeography().toResult()
        return response.map { it.toDomain() }
    }

    override suspend fun getPlaceGeographies(): Result<List<PlaceGeography>> {
        val response = placeDataSource.fetchPlaceGeographies().toResult()
        return response.map { placeGeographies -> placeGeographies.map { it.toDomain() } }
    }
}
