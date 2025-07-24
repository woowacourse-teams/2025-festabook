package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.Place

interface PlaceListRepository {
    suspend fun getPlaces(): Result<List<Place>>
}
