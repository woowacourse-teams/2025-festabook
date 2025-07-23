package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Place

interface PlaceListRepository {
    suspend fun fetchPlaces(): Result<List<Place>>
}
