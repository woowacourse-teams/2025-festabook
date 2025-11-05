package com.daedan.festabook.presentation.placeMap.model

import com.daedan.festabook.domain.model.Coordinate
import com.naver.maps.geometry.LatLng

data class CoordinateUiModel(
    val latitude: Double,
    val longitude: Double,
)

fun CoordinateUiModel.toLatLng() = LatLng(latitude, longitude)

fun Coordinate.toUiModel() =
    CoordinateUiModel(
        latitude = latitude,
        longitude = longitude,
    )
