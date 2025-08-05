package com.daedan.festabook.presentation.placeList.placeMap

import com.daedan.festabook.presentation.placeList.model.PlaceCategoryUiModel

fun interface OnMarkerClickListener {
    fun onMarkerListener(
        placeId: Long,
        category: PlaceCategoryUiModel,
    )
}
