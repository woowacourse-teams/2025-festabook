package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.OrganizationGeography
import com.daedan.festabook.domain.model.Place
import com.daedan.festabook.domain.model.PlaceGeography
import com.daedan.festabook.domain.model.TimeTag

interface PlaceListRepository {
    suspend fun getTimeTags(): Result<List<TimeTag>>

    suspend fun getPlaces(): Result<List<Place>>

    suspend fun getPlaceGeographies(): Result<List<PlaceGeography>>

    suspend fun getOrganizationGeography(): Result<OrganizationGeography>
}
