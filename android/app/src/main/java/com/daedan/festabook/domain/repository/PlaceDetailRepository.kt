package com.daedan.festabook.domain.repository

import com.daedan.festabook.domain.model.PlaceDetail

interface PlaceDetailRepository {
    suspend fun fetchPlaceDetail(placeId: Long): Result<PlaceDetail>
}
