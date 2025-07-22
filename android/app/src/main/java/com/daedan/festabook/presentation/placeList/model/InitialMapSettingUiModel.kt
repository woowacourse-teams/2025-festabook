package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.domain.model.PlaceGeography

data class InitialMapSettingUiModel(
    val zoom: Int,
    val initialCenter: CoordinateUiModel,
    val border: List<CoordinateUiModel>,
)

fun PlaceGeography.toUiModel() =
    InitialMapSettingUiModel(
        zoom = zoom,
        initialCenter = initialCenter.toUiModel(),
        border = polygonHoleBoundary.map { it.toUiModel() },
    )
