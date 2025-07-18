package com.daedan.festabook.presentation.placeList.model

data class InitialMapSettingUiModel(
    val zoom: Int,
    val initialCenter: CoordinateUiModel,
    val border: List<CoordinateUiModel>,
)
