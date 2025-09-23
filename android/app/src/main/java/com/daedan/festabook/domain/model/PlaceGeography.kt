package com.daedan.festabook.domain.model

data class PlaceGeography(
    val id: Long,
    val category: PlaceCategory,
    val markerCoordinate: Coordinate,
    val title: String,
    val timeTags: List<TimeTag>,
)
