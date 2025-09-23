package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.domain.model.PlaceGeography

data class PlaceCoordinateUiModel(
    val placeId: Long,
    val coordinate: CoordinateUiModel,
    val category: PlaceCategoryUiModel,
    val title: String,
    val timeTagId: List<Long> = emptyList(),
)

fun PlaceGeography.toUiModel() =
    PlaceCoordinateUiModel(
        placeId = id,
        coordinate = markerCoordinate.toUiModel(),
        category = category.toUiModel(),
        title = title,
        timeTagId = timeTag.map { it.timeTagId },
    )
