package com.daedan.festabook.domain.model

data class PlaceGeography(
    val zoom: Int,
    val initialCenter: Coordinate,
    val polygonHoleBoundary: List<Coordinate>,
)
