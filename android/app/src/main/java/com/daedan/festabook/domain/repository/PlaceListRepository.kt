package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.OrganizationGeography
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceGeography

interface PlaceListRepository {
    suspend fun getPlaces(): Result<List<Place>>

    suspend fun getPlaceGeographies(): Result<List<PlaceGeography>>

    suspend fun getOrganizationGeography(): Result<OrganizationGeography>
}
