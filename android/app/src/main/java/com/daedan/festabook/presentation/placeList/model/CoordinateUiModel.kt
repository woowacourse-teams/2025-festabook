package com.daedan.festabook.presentation.placeList.model

import com.naver.maps.geometry.LatLng

data class CoordinateUiModel(
    val latitude: Double,
    val longitude: Double,
)

fun CoordinateUiModel.toLatLng() = LatLng(latitude, longitude)
