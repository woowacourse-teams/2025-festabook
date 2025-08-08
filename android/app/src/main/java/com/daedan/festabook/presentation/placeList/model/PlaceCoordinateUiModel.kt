package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.domain.model.PlaceGeography

data class PlaceCoordinateUiModel(
    val placeId: Long,
    val coordinate: CoordinateUiModel,
    val category: PlaceCategoryUiModel,
)

fun PlaceGeography.toUiModel() =
    PlaceCoordinateUiModel(
        placeId = id,
        coordinate = markerCoordinate.toUiModel(),
        category = category.toUiModel(),
    )
