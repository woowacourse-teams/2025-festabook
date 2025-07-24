package com.daedan.festabook.presentation.placeList.model

import com.daedan.festabook.domain.model.OrganizationGeography

data class InitialMapSettingUiModel(
    val zoom: Int,
    val initialCenter: CoordinateUiModel,
    val border: List<CoordinateUiModel>,
    val placeCoordinates: List<PlaceCoordinateUiModel> = emptyList<PlaceCoordinateUiModel>(),
)

fun OrganizationGeography.toUiModel() =
    InitialMapSettingUiModel(
        zoom = zoom,
        initialCenter = initialCenter.toUiModel(),
        border = polygonHoleBoundary.map { it.toUiModel() },
    )
