package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.domain.model.PlaceGeography

data class PlaceCoordinateUiModel(
    val coordinate: CoordinateUiModel,
    val category: PlaceCategoryUiModel,
)

fun PlaceGeography.toUiModel() =
    PlaceCoordinateUiModel(
        coordinate = markerCoordinate.toUiModel(),
        category = category.toUiModel(),
    )
