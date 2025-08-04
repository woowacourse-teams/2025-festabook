package com.daedan.festabook.data.repository

import com.daedan.festabook.data.datasource.remote.place.PlaceDataSource
import com.daedan.festabook.data.model.response.organization.toDomain
import com.daedan.festabook.data.model.response.place.toDomain
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
        return response.mapCatching { places -> places.map { it.toDomain() } }
    }

    override suspend fun getOrganizationGeography(): Result<OrganizationGeography> {
        val response = placeDataSource.fetchOrganizationGeography().toResult()
        return response.mapCatching { it.toDomain() }
    }

    override suspend fun getPlaceGeographies(): Result<List<PlaceGeography>> {
        val response = placeDataSource.fetchPlaceGeographies().toResult()
        return response.mapCatching { placeGeographies ->
            placeGeographies.map { it.toDomain() }
        }
    }
}
